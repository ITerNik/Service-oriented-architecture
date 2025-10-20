import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import CityManagementApp from './CityManagementApp.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <CityManagementApp />
  </StrictMode>,
)
