import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Collection } from 'app/common/collection';
import { CollectionToAdd } from 'app/common/collectionToAdd';
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

  searchCollection(title: string, city: string, street: string, itemName: string, categories: string): Observable<Collection[]> {
    return this.httpClient.get<Collection[]>(`${this.getUrl}?title=${title}&city=${city}&street=${street}&itemName=${itemName}&categories=${categories}`);
  }
}
