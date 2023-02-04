import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent implements OnInit {

  categories = [
    { value: 'food', label: 'Jedzenie'}, 
    { value: 'clothes', label: 'Ubrania'},
    { value: 'animals', label: 'Zwierzęta'},
    { value: 'children', label: 'Dzieci'},
    { value: 'electronic', label: 'Elektronika'},
    { value: 'medicine', label: 'Lekarstwa'},
    { value: 'other', label: 'Inne'},
  ];

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  searchCollections(category: string) {
    let navigationExtras = {state: {data: category}};
    this.router.navigateByUrl("collections", navigationExtras);
  }

}
