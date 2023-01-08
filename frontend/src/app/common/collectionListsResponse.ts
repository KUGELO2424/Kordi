import { Collection } from "./collection";

export class CollectionListResponse {
    content: Collection[];
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
}