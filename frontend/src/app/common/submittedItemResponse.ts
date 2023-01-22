import { SubmittedItem } from "./submittedItem";

export class SubmittedItemListResponse {
    content: SubmittedItem[];
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
}