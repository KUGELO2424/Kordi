import { Item } from "./itemToAdd";
import { Location } from "./location";

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
    image: string;
    progress: number;
}