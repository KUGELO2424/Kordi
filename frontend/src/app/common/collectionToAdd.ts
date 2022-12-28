import { ItemToAdd, NewItem } from "./itemToAdd";
import { Location, LocationToAdd } from "./location";

export class CollectionToAdd {
    title: string;
    description: string;
    endDate: Date;
    userId: number;
    addresses: LocationToAdd[];
    items: ItemToAdd[];
    image: Blob;
}