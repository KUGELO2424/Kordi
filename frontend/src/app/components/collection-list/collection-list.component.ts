import { Component, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
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

  constructor(private formBuilder: UntypedFormBuilder, private collectionService: CollectionService) { }

  ngOnInit(): void {
    this.search();
  }

  search() {
    this.collectionService.searchCollection(this.searchItem, this.searchCity, this.searchStreet, this.searchItem, 
      this.getChoosenCategoriesAsString()).subscribe({
        next: (data) => {
          this.collections = data;
          console.log(this.collections);
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
        let category = Object.keys(ItemCategory)[index];
        if (categorires == "") {
          categorires = categorires.concat(category)
        } else {
          categorires = categorires.concat("," + category)
        }
      }
    });
   return categorires;
  }

}
