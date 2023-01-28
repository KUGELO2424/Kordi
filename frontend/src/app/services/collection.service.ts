import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Collection } from 'app/common/collection';
import { CollectionListResponse } from 'app/common/collectionListsResponse';
import { CollectionToAdd } from 'app/common/collectionToAdd';
import { SubmittedItem } from 'app/common/submittedItem';
import { Comment } from 'app/common/comment';
import { CommentListResponse } from 'app/common/commentListResponse';
import { environment } from 'environments/environment';
import { Observable, of } from 'rxjs';
import { Item, ItemToAdd } from 'app/common/itemToAdd';
import { SubmittedItemListResponse } from 'app/common/submittedItemResponse';
import { UpdateCollection } from 'app/common/updateCollection';
import { ItemUpdate } from 'app/common/itemUpdate';

@Injectable({
  providedIn: 'root'
})
export class CollectionService {

  private addUrl = environment.baseUrl + '/collections';
  private getUrl = environment.baseUrl + '/collections';
  private getUserCollections = environment.baseUrl + '/users';

  constructor(private httpClient: HttpClient) { }

  addCollection(collection: CollectionToAdd) {
    return this.httpClient.post<any>(this.addUrl, collection)
  }

  addItem(item: ItemToAdd, collectionId: string) {
    return this.httpClient.post<any>(`${this.addUrl}/${collectionId}`, item)
  }

  searchCollection(title: string, city: string, street: string, itemName: string, categories: string, pageNumber: number, 
    pageSize: number, sortBy: string, sortDirection: string): Observable<CollectionListResponse> {
    return this.httpClient.get<CollectionListResponse>(`${this.getUrl}?title=${title}&city=${city}&street=${street}&itemName=${itemName}&categories=${categories}&pageNo=${pageNumber}&pageSize=${pageSize}&sortBy=${sortBy}&sortDirection=${sortDirection}`);
  }

  getCollectionById(id: string): Observable<Collection> {
    return this.httpClient.get<Collection>(`${this.getUrl}/${id}`);
  }

  getCollectionForLoggedUser(): Observable<Collection[]> {
    let username = sessionStorage.getItem("username");
    return this.httpClient.get<Collection[]>(`${this.getUserCollections}/${username}/collections`);
  }

  getCommentsFromCollection(id: string, pageNumber: number, pageSize: number): Observable<CommentListResponse> {
    return this.httpClient.get<CommentListResponse>(`${this.getUrl}/${id}/comments?pageNo=${pageNumber}&pageSize=${pageSize}`);
  }

  getSubmittedItemsFromCollection(id: string): Observable<SubmittedItem[]> {
    return this.httpClient.get<SubmittedItem[]>(`${this.getUrl}/${id}/submittedItems`);
  }

  getSubmittedItemsForUser(username: string, pageNumber: number, pageSize: number): Observable<SubmittedItemListResponse> {
    return this.httpClient.get<SubmittedItemListResponse>(`${this.getUrl}/submittedItems?username=${username}&pageNo=${pageNumber}&pageSize=${pageSize}`);
  }

  getLastNSubmittedItemsFromCollection(id: string, number: number): Observable<SubmittedItem[]> {
    return this.httpClient.get<SubmittedItem[]>(`${this.getUrl}/${id}/submittedItems?numberOfSubmittedItems=${number}`);
  }

  donateItem(id:string, items: SubmittedItem[]) {
    return this.httpClient.post<SubmittedItem[]>(`${this.getUrl}/${id}/items/submit`, items);
  }

  addComment(id:string, comment: Comment) {
    return this.httpClient.post<Comment>(`${this.getUrl}/${id}/comments`, comment);
  }

  updateCollection(collectionToUpdate: UpdateCollection) {
    return this.httpClient.patch<Collection>(`${this.addUrl}`, collectionToUpdate);
  }

  updateCollectionItem(item: ItemUpdate, collectionId: string, itemId: string) {
    return this.httpClient.patch<Collection>(`${this.addUrl}/${collectionId}/items/${itemId}`, item);
  }

  getCollectionProgress(items: Item[]): Observable<number> {
    let current = 0;
    let max = 0;
    for(let item of items) {
      if (item.type.toString().toLocaleLowerCase() !== 'unlimited') {
        current = current + item.currentAmount;
        max = max + item.maxAmount;
      }
    }
    return of((current / max) * 100);
  }
}
