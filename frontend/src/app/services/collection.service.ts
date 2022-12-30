import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Collection } from 'app/common/collection';
import { CollectionToAdd } from 'app/common/collectionToAdd';
import { environment } from 'environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CollectionService {

  private addUrl = environment.baseUrl + '/collections';

  constructor(private httpClient: HttpClient) { }

  addCollection(collection: CollectionToAdd) {
    return this.httpClient.post<Collection>(this.addUrl, collection)
  }
}
