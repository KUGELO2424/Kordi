import { Comment } from "./comment";

export class CommentListResponse {
    content: Comment[];
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
}