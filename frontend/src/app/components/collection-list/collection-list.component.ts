import { Component, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { DomSanitizer } from '@angular/platform-browser';
import { Collection } from 'app/common/collection';
import { ItemCategory } from 'app/common/itemToAdd';
import { CollectionService } from 'app/services/collection.service';

@Component({
  selector: 'app-collection-list',
  templateUrl: './collection-list.component.html',
  styleUrls: ['./collection-list.component.css']
})
export class CollectionListComponent implements OnInit {

  collections: Collection[] = [];

  searchTerm = "";
  searchCity = "";
  searchStreet = "";
  searchItem = "";

  pageNumber: number = 0;
  pageSize: number = 10;
  totalRecords: number = 10;

  categories = [
    { value: 'food', label: 'Jedzenie'}, 
    { value: 'clothes', label: 'Ubrania'},
    { value: 'animals', label: 'Zwierzęta'},
    { value: 'children', label: 'Dzieci'},
    { value: 'electronic', label: 'Elektronika'},
    { value: 'medicine', label: 'Lekarstwa'},
    { value: 'other', label: 'Inne'},
  ];

  categoriesForm = this.formBuilder.group({
    food: false,
    clothes: false,
    animals: false,
    children: false,
    electronic: false,
    medicine: false,
    other: false,
  });

  constructor(private formBuilder: UntypedFormBuilder, private collectionService: CollectionService, private sanitizer: DomSanitizer) { }

  ngOnInit(): void {
    this.search();
  }

  search() {
    this.collectionService.searchCollection(this.searchTerm, this.searchCity, this.searchStreet, this.searchItem, 
      this.getChoosenCategoriesAsString(), this.pageNumber, this.pageSize).subscribe({
        next: (data) => {
          this.collections = data.content;
          this.pageSize = data.size;
          this.totalRecords = data.totalElements;
        },
        error: (error) => {
          console.log(error);
        }
      })
  }

  getChoosenCategoriesAsString() {
    let categorires = "";
    Object.keys(this.categoriesForm.controls).forEach((key, index) => {
      if (this.categoriesForm.controls[key].value) {
        if (categorires == "") {
          categorires = categorires.concat(key.toLocaleUpperCase())
        } else {
          categorires = categorires.concat("," + key.toLocaleUpperCase())
        }
      }
    });
    return categorires;
  }

  getImageFromBase64(dataURI: string) {
    let objectURL = 'data:image/jpeg;base64,' + dataURI;
    return this.sanitizer.bypassSecurityTrustUrl(objectURL);
  }
  
  changePage(event: any) {
    this.pageSize = event.rows;
    this.pageNumber = event.page;
    this.search();
  }

}
