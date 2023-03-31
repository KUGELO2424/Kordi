import { ViewportScroller } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Collection, CollectionStatus } from 'app/common/collection';
import { CollectionListStateService } from 'app/services/collection-list-state.service';
import { CollectionService } from 'app/services/collection.service';
import { StateService } from 'app/services/state.service';

@Component({
  selector: 'app-collection-list',
  templateUrl: './collection-list.component.html',
  styleUrls: ['./collection-list.component.css']
})
export class CollectionListComponent implements OnInit {

  status = CollectionStatus;

  collections: Collection[] = [];

  searchTerm = "";
  searchCity = "";
  searchStreet = "";
  searchItem = "";

  pageNumber: number = 0;
  pageSize: number = 10;
  totalRecords: number = 10;

  state: any;

  sortOptions: any[] = [];
  selectedSort: any = { label: this.translate.instant('collections.sort_popularity'), field: 'donates', direction: 'desc'};

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

  constructor(private formBuilder: UntypedFormBuilder, private collectionService: CollectionService, private router: Router,
    private sanitizer: DomSanitizer, private scroller: ViewportScroller, private translate: TranslateService,
    private stateService: CollectionListStateService) {
      const navigation = this.router.getCurrentNavigation();
      const state = navigation?.extras.state as {data: string};
      if (state !== undefined) {
        this.categoriesForm.controls[state.data].setValue(true);
      }
      
      this.translate.get('add-collection.locations').subscribe(() => {
        this.sortOptions = [
          { label: this.translate.instant('collections.sort_popularity'), field: 'donates', direction: 'desc'},
          { label: this.translate.instant('collections.sort_newest'), field: 'startTime', direction: 'desc'},
          { label: this.translate.instant('collections.sort_oldest'), field: 'startTime', direction: 'asc'},
        ]
        this.selectedSort = this.sortOptions[0];
      })
    }

  ngOnInit(): void {
    this.state = this.stateService.state$.getValue() || {}
    this.stateService.state$.next("");
    if (Object.keys(this.state).length !== 0) {
      if ("filtr" in this.state) {
        this.setFiltrAndSortOptions(this.state.filtr);
      }
    }
    this.search();
  }

  search() {
    this.collectionService.searchCollection(this.searchTerm, this.searchCity, this.searchStreet, this.searchItem, 
      this.getChoosenCategoriesAsString(), this.pageNumber, this.pageSize, this.selectedSort.field, this.selectedSort.direction)
      .subscribe({
        next: (data) => {
          this.collections = data.content;
          this.pageSize = data.size;
          this.totalRecords = data.totalElements;
          this.getCollectionsProgress();
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
    this.scroll();
    this.pageSize = event.rows;
    this.pageNumber = event.page;
    this.search();
  }

  scroll() {
    this.scroller.scrollToAnchor("collectionList");
  }

  getCollectionsProgress() {
    for(let collection of this.collections) {
      this.collectionService.getCollectionProgress(collection.items).subscribe({
        next: (data) => {
          collection.progress = data;
        }
      })
    }
  }

  getDaysToEnd(collection: Collection) {
    const msInDay = 24 * 60 * 60 * 1000;
    if (collection.endTime != null) {
      let now = new Date().getTime();
      let endTime = Date.parse(collection.endTime.toString());
      let diffTime = endTime - now;
      return Math.round((diffTime / msInDay));
    }
    return 0;
  }

  openCollection(collectionId: number) {
    this.saveState();
    this.router.navigateByUrl("/collections/" + collectionId);
  }

  saveState() {
    const filtr = {
      searchTerm: this.searchTerm,
      city: this.searchCity,
      street: this.searchStreet,
      item: this.searchItem,
      categories: this.categoriesForm,
      sort: this.selectedSort
    }
    this.state.filtr = filtr
    this.stateService.state$.next(this.state);
  }

  setFiltrAndSortOptions(filtr: any) {
    this.searchTerm = filtr.searchTerm;
    this.searchCity = filtr.city;
    this.searchStreet = filtr.street;
    this.searchItem = filtr.item;
    this.categoriesForm = filtr.categories;
    this.selectedSort = filtr.sort
  }

}
