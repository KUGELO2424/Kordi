import { Item } from "./itemToAdd";

export class Collection {
    id: number;
    title: string;
    description: string;
    startTime: Date
    endTime: Date;
    userId: number;
    userFirstname: string;
    userLastname: string;
    addresses: Location[];
    items: Item[];
    image: Blob;
}