import { Component, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
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
    { value: 'clothes', label: 'Ubrania'},
    { value: 'animals', label: 'Zwierzęta'},
    { value: 'children', label: 'Dzieci'},
    { value: 'electronic', label: 'Elektronika'},
    { value: 'medicine', label: 'Lekarstwa'},
    { value: 'other', label: 'Inne'},
  ];

  categoriesForm = this.formBuilder.group({
    food: false,
    cloths: false,
    animals: false,
    children: false,
    electronic: false,
    medicine: false,
    other: false,
  });

  constructor(private formBuilder: UntypedFormBuilder) { }

  ngOnInit(): void {
  }

}