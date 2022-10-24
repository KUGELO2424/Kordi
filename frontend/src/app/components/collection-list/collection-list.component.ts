import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';

@Component({
  selector: 'app-collection-list',
  templateUrl: './collection-list.component.html',
  styleUrls: ['./collection-list.component.css']
})
export class CollectionListComponent implements OnInit {

  searchTerm = "";
  searchCity = "";
  searchStreet = "";
  searchItem = "";

  categories = [
    { value: 'food', label: 'Jedzenie'}, 
    { value: 'cloths', label: 'Ubrania'},
    { value: 'animals', label: 'Zwierzęta'},
    { value: 'home', label: 'Dom'},
    { value: 'accesories', label: 'Akcesoria'},
  ];

  categoriesForm = this.formBuilder.group({
    food: false,
    cloths: false,
    animals: false,
    home: false,
    accesories: false
  });

  constructor(private formBuilder: FormBuilder) { }

  ngOnInit(): void {
  }

}
