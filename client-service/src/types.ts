export type City = {
    id?: string;
    name: string;
    population: number;
    area: number;
    climate: string;
    coordinates: {
        x: number;
        y: number;
    };
    creationDate?: string;
    metersAboveSeaLevel: number,
    capital: boolean,
    agglomeration: number,
    governor: {
        height: number;
        birthday: string;
    };
};