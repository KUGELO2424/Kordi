import { ItemToAdd } from "./itemToAdd";
import { LocationToAdd } from "./location";

export class CollectionToAdd {
    title: string;
    description: string;
    endTime: Date;
    addresses: LocationToAdd[];
    items: ItemToAdd[];
    image: string;
}