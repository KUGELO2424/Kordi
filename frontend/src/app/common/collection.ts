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
    status: CollectionStatus;
}

export enum CollectionStatus {
    IN_PROGRESS = "IN_PROGRESS",
    COMPLETED = "COMPLETED",
    ARCHIVED = "ARCHIVED"
}