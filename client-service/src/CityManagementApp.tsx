import { useState, useEffect, type FormEventHandler } from 'react';
import { Plus, Trash2, Edit, MapPin, AlertCircle } from 'lucide-react';
import type { City } from "./types.ts";

const API_BASE_URL = import.meta.env.API_URL ?? 'http://localhost:8080/api';

export default function CityManagementApp() {
  const [cities, setCities] = useState<City[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [sortField, setSortField] = useState('id');
  const [sortOrder, setSortOrder] = useState('asc');
  const [filters, setFilters] = useState<Record<string, string>>({});

  const [showModal, setShowModal] = useState(false);
  const [editingCity, setEditingCity] = useState<City | null>(null);
  const [formData, setFormData] = useState<City>(getEmptyCity());

  const [routeDistance, setRouteDistance] = useState<string | null>(null);

  useEffect(() => {
    fetchCities();
  }, [page, size, sortField, sortOrder, filters]);

  function getEmptyCity() {
    return {
      name: '',
      coordinates: { x: 0, y: 0 },
      area: 1,
      population: 1,
      metersAboveSeaLevel: 0,
      capital: false,
      agglomeration: 0,
      climate: 'HUMIDCONTINENTAL',
      governor: { height: 1, birthday: '' }
    };
  }

  async function fetchCities() {
    setLoading(true);
    setError('');

    try {
      const sortParam = sortOrder === 'desc' ? `-${sortField}` : sortField;
      let url = `${API_BASE_URL}/cities?page=${page}&size=${size}&sort=${sortParam}`;

      Object.keys(filters).forEach(key => {
        if (filters[key]) {
          url += `&${key}=${encodeURIComponent(filters[key])}`;
        }
      });

      const response = await fetch(url);
      if (!response.ok) {
        setError('Ошибка загрузки данных');
        return;
      }

      const data = await response.json();
      setCities(data);
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  }

  const handleSubmit: FormEventHandler<HTMLFormElement> = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      const url = editingCity
        ? `${API_BASE_URL}/cities/${editingCity.id}`
        : `${API_BASE_URL}/cities`;

      const method = editingCity ? 'PUT' : 'POST';

      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
      });

      if (!response.ok) {
        const errorData = await response.json();
        setError(errorData.message || 'Ошибка при сохранении');
        return;
      }

      setSuccess(editingCity ? 'Город обновлен' : 'Город создан');
      setShowModal(false);
      setEditingCity(null);
      setFormData(getEmptyCity());
      await fetchCities();
    } catch (err) {
      setError((err as Error).message);
    }
  }

  async function handleDelete(id: City['id']) {
    if (!confirm('Удалить город?')) return;

    try {
      const response = await fetch(`${API_BASE_URL}/cities/${id}`, {
        headers: { 'Content-Type': 'application/json' },
        method: 'DELETE'
      });

      if (!response.ok) {
        setError('Ошибка при удалении');
        return;
      }

      setSuccess('Город удален');
      await fetchCities();
    } catch (err) {
      setError((err as Error).message);
    }
  }

  async function calculateToMaxPopulated() {
    try {
      const response = await fetch(`${API_BASE_URL}/route/calculate/to-max-populated`);
      if (!response.ok) {
        setError('Ошибка расчета маршрута');
        return;
      }

      const data = await response.json();
      setRouteDistance(`Расстояние до самого населенного города: ${data.distance.toFixed(2)}`);
    } catch (err) {
      setError((err as Error).message);
    }
  }

  async function calculateBetweenOldestNewest() {
    try {
      const response = await fetch(`${API_BASE_URL}/route/calculate/between-oldest-and-newest`);
      if (!response.ok) {
        setError('Ошибка расчета маршрута');
        return;
      }

      const data = await response.json();
      setRouteDistance(`Расстояние между старейшим и новейшим городом: ${data.distance.toFixed(2)}`);
    } catch (err) {
      setError((err as Error).message);
    }
  }

  function openEditModal(city: City) {
    setEditingCity(city);
    setFormData({ ...city });
    setShowModal(true);
  }

  function openCreateModal() {
    setEditingCity(null);
    setFormData(getEmptyCity());
    setShowModal(true);
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 p-6">
      <div className="max-w-7xl mx-auto">
        <header className="bg-white rounded-lg shadow-lg p-6 mb-6">
          <h1 className="text-3xl font-bold text-indigo-900 mb-2">
            Управление городами
          </h1>
          <p className="text-gray-600">Система управления коллекцией городов</p>
        </header>

        {error && (
          <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 mb-4 rounded">
            <div className="flex items-center">
              <AlertCircle className="w-5 h-5 mr-2" />
              <span>{error}</span>
            </div>
          </div>
        )}

        {success && (
          <div className="bg-green-100 border-l-4 border-green-500 text-green-700 p-4 mb-4 rounded">
            {success}
          </div>
        )}

        {routeDistance && (
          <div className="bg-blue-100 border-l-4 border-blue-500 text-blue-700 p-4 mb-4 rounded">
            <MapPin className="inline w-5 h-5 mr-2" />
            {routeDistance}
          </div>
        )}

        <div className="bg-white rounded-lg shadow-lg p-6 mb-6">
          <div className="flex flex-wrap gap-4 mb-6">
            <button
              onClick={openCreateModal}
              className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg flex items-center gap-2 transition"
            >
              <Plus className="w-5 h-5" />
              Создать город
            </button>

            <button
              onClick={calculateToMaxPopulated}
              className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg transition"
            >
              Маршрут до макс. населения
            </button>

            <button
              onClick={calculateBetweenOldestNewest}
              className="bg-purple-600 hover:bg-purple-700 text-white px-4 py-2 rounded-lg transition"
            >
              Между старейшим и новейшим
            </button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
            <input
              type="text"
              placeholder="Фильтр по имени"
              className="border rounded-lg px-4 py-2"
              onChange={(e) => setFilters({ ...filters, name: e.target.value })}
            />

            <select
              className="border rounded-lg px-4 py-2"
              onChange={(e) => setSortField(e.target.value)}
              value={sortField}
            >
              <option value="id">Сортировка: ID</option>
              <option value="name">Сортировка: Имя</option>
              <option value="population">Сортировка: Население</option>
              <option value="area">Сортировка: Площадь</option>
              <option value="creationDate">Сортировка: Дата создания</option>
            </select>

            <select
              className="border rounded-lg px-4 py-2"
              onChange={(e) => setSortOrder(e.target.value)}
              value={sortOrder}
            >
              <option value="asc">По возрастанию</option>
              <option value="desc">По убыванию</option>
            </select>

            <select
              className="border rounded-lg px-4 py-2"
              onChange={(e) => setSize(parseInt(e.target.value))}
              value={size}
            >
              <option value="5">5 на странице</option>
              <option value="10">10 на странице</option>
              <option value="20">20 на странице</option>
              <option value="50">50 на странице</option>
            </select>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-lg overflow-hidden">
          {loading ? (
            <div className="p-8 text-center">Загрузка...</div>
          ) : cities.length === 0 ? (
            <div className="p-8 text-center text-gray-500">Городов нет</div>
          ) : (
            <>
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead className="bg-indigo-600 text-white">
                    <tr>
                      <th className="px-4 py-3 text-left">ID</th>
                      <th className="px-4 py-3 text-left">Название</th>
                      <th className="px-4 py-3 text-left">Население</th>
                      <th className="px-4 py-3 text-left">Площадь</th>
                      <th className="px-4 py-3 text-left">Климат</th>
                      <th className="px-4 py-3 text-left">Координаты</th>
                      <th className="px-4 py-3 text-left">Дата создания</th>
                      <th className="px-4 py-3 text-right">Действия</th>
                    </tr>
                  </thead>
                  <tbody>
                    {cities.map((city, idx) => (
                      <tr key={city.id} className={idx % 2 === 0 ? 'bg-gray-50' : 'bg-white'}>
                        <td className="px-4 py-3">{city.id}</td>
                        <td className="px-4 py-3 font-medium">{city.name}</td>
                        <td className="px-4 py-3">{city.population.toLocaleString()}</td>
                        <td className="px-4 py-3">{city.area}</td>
                        <td className="px-4 py-3">
                          <span className="px-2 py-1 bg-indigo-100 text-indigo-800 rounded text-sm">
                            {city.climate}
                          </span>
                        </td>
                        <td className="px-4 py-3 text-sm">
                          ({city.coordinates.x.toFixed(1)}, {city.coordinates.y.toFixed(1)})
                        </td>
                        <td className="px-4 py-3 text-sm">{city.creationDate}</td>
                        <td className="px-4 py-3 text-right">
                          <button
                            onClick={() => openEditModal(city)}
                            className="text-blue-600 hover:text-blue-800 mr-3"
                          >
                            <Edit className="w-4 h-4 inline" />
                          </button>
                          <button
                            onClick={() => handleDelete(city.id)}
                            className="text-red-600 hover:text-red-800"
                          >
                            <Trash2 className="w-4 h-4 inline" />
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              <div className="bg-gray-50 px-6 py-4 flex justify-between items-center">
                <button
                  onClick={() => setPage(Math.max(0, page - 1))}
                  disabled={page === 0}
                  className="px-4 py-2 bg-indigo-600 text-white rounded disabled:bg-gray-300"
                >
                  Предыдущая
                </button>
                <span className="text-gray-700">Страница {page + 1}</span>
                <button
                  onClick={() => setPage(page + 1)}
                  disabled={cities.length < size}
                  className="px-4 py-2 bg-indigo-600 text-white rounded disabled:bg-gray-300"
                >
                  Следующая
                </button>
              </div>
            </>
          )}
        </div>

        {showModal && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-lg max-w-2xl w-full max-h-screen overflow-y-auto p-6">
              <h2 className="text-2xl font-bold mb-4">
                {editingCity ? 'Редактировать город' : 'Создать город'}
              </h2>

              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium mb-1">Название *</label>
                  <input
                    type="text"
                    required
                    className="w-full border rounded px-3 py-2"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  />
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium mb-1">Координата X *</label>
                    <input
                      type="number"
                      step="0.0001"
                      required
                      className="w-full border rounded px-3 py-2"
                      value={formData.coordinates.x}
                      onChange={(e) => setFormData({
                        ...formData,
                        coordinates: { ...formData.coordinates, x: parseFloat(e.target.value) }
                      })}
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium mb-1">Координата Y * (≥ -194)</label>
                    <input
                      type="number"
                      step="0.0001"
                      min="-194"
                      required
                      className="w-full border rounded px-3 py-2"
                      value={formData.coordinates.y}
                      onChange={(e) => setFormData({
                        ...formData,
                        coordinates: { ...formData.coordinates, y: parseFloat(e.target.value) }
                      })}
                    />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium mb-1">Площадь * (≥ 1)</label>
                    <input
                      type="number"
                      min="1"
                      required
                      className="w-full border rounded px-3 py-2"
                      value={formData.area}
                      onChange={(e) => setFormData({ ...formData, area: parseInt(e.target.value) })}
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium mb-1">Население * (≥ 1)</label>
                    <input
                      type="number"
                      min="1"
                      required
                      className="w-full border rounded px-3 py-2"
                      value={formData.population}
                      onChange={(e) => setFormData({ ...formData, population: parseInt(e.target.value) })}
                    />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium mb-1">Метров над уровнем моря</label>
                    <input
                      type="number"
                      className="w-full border rounded px-3 py-2"
                      value={formData.metersAboveSeaLevel}
                      onChange={(e) => setFormData({ ...formData, metersAboveSeaLevel: parseInt(e.target.value) })}
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium mb-1">Агломерация</label>
                    <input
                      type="number"
                      step="0.01"
                      className="w-full border rounded px-3 py-2"
                      value={formData.agglomeration}
                      onChange={(e) => setFormData({ ...formData, agglomeration: parseFloat(e.target.value) })}
                    />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium mb-1">Климат *</label>
                    <select
                      required
                      className="w-full border rounded px-3 py-2"
                      value={formData.climate}
                      onChange={(e) => setFormData({ ...formData, climate: e.target.value })}
                    >
                      <option value="HUMIDCONTINENTAL">Влажный континентальный</option>
                      <option value="TUNDRA">Тундра</option>
                      <option value="DESERT">Пустыня</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-medium mb-1">Столица</label>
                    <input
                      type="checkbox"
                      className="mt-2 w-5 h-5"
                      checked={formData.capital}
                      onChange={(e) => setFormData({ ...formData, capital: e.target.checked })}
                    />
                  </div>
                </div>

                <div className="border-t pt-4">
                  <h3 className="font-semibold mb-3">Губернатор</h3>
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium mb-1">Рост * (≥ 1)</label>
                      <input
                        type="number"
                        step="0.1"
                        min="1"
                        required
                        className="w-full border rounded px-3 py-2"
                        value={formData.governor.height}
                        onChange={(e) => setFormData({
                          ...formData,
                          governor: { ...formData.governor, height: parseFloat(e.target.value) }
                        })}
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium mb-1">Дата рождения</label>
                      <input
                        type="date"
                        className="w-full border rounded px-3 py-2"
                        value={formData.governor.birthday || ''}
                        onChange={(e) => setFormData({
                          ...formData,
                          governor: { ...formData.governor, birthday: e.target.value }
                        })}
                      />
                    </div>
                  </div>
                </div>

                <div className="flex gap-4 pt-4">
                  <button
                    type="submit"
                    className="flex-1 bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded"
                  >
                    {editingCity ? 'Обновить' : 'Создать'}
                  </button>
                  <button
                    type="button"
                    onClick={() => setShowModal(false)}
                    className="flex-1 bg-gray-300 hover:bg-gray-400 px-4 py-2 rounded"
                  >
                    Отмена
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}