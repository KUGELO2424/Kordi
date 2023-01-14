import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Collection } from 'app/common/collection';
import { CollectionListResponse } from 'app/common/collectionListsResponse';
import { CollectionToAdd } from 'app/common/collectionToAdd';
import { SubmittedItem } from 'app/common/submittedItem';
import { CommentListResponse } from 'app/common/commentListResponse';
import { environment } from 'environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CollectionService {

  private addUrl = environment.baseUrl + '/collections';
  private getUrl = environment.baseUrl + '/collections';

  constructor(private httpClient: HttpClient) { }

  addCollection(collection: CollectionToAdd) {
    return this.httpClient.post<Collection>(this.addUrl, collection)
  }

  searchCollection(title: string, city: string, street: string, itemName: string, categories: string, pageNumber: number, pageSize: number): Observable<CollectionListResponse> {
    return this.httpClient.get<CollectionListResponse>(`${this.getUrl}?title=${title}&city=${city}&street=${street}&itemName=${itemName}&categories=${categories}&pageNo=${pageNumber}&pageSize=${pageSize}`);
  }

  getCollectionById(id: string): Observable<Collection> {
    return this.httpClient.get<Collection>(`${this.getUrl}/${id}`);
  }

  getCommentsFromCollection(id: string, pageNumber: number, pageSize: number): Observable<CommentListResponse> {
    return this.httpClient.get<CommentListResponse>(`${this.getUrl}/${id}/comments?pageNo=${pageNumber}&pageSize=${pageSize}`);
  }

  getSubmittedItemsFromCollection(id: string): Observable<SubmittedItem[]> {
    return this.httpClient.get<SubmittedItem[]>(`${this.getUrl}/${id}/submittedItems`);
  }

  getLastNSubmittedItemsFromCollection(id: string, number: number): Observable<SubmittedItem[]> {
    return this.httpClient.get<SubmittedItem[]>(`${this.getUrl}/${id}/submittedItems?numberOfSubmittedItems=${number}`);
  }
}
