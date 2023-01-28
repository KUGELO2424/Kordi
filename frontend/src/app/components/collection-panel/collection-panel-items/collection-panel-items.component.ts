import { ViewportScroller } from '@angular/common';
import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, UntypedFormControl, UntypedFormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Item, ItemCategory, ItemToAdd, ItemType } from 'app/common/itemToAdd';
import { ItemUpdate } from 'app/common/itemUpdate';
import { CollectionService } from 'app/services/collection.service';
import { PanelStateService } from 'app/services/panel-state.service';
import { Message, MessageService } from 'primeng/api';
import { Table } from 'primeng/table';

@Component({
  selector: 'app-collection-panel-items',
  templateUrl: './collection-panel-items.component.html',
  styleUrls: ['./collection-panel-items.component.css']
})
export class CollectionPanelItemsComponent implements OnInit {
  
  @ViewChild('dt') table: Table;
  @Input() collectionId: number;
  @Input() itemsData: Item[];

  items: Item[] = [];
  pageSize: number = 10;
  totalRecords: number;
  page: number = 0;

  submitted: boolean;
  itemDialog: boolean;

  editted: boolean;
  itemUpdateDialog: boolean;
  itemSuffix: string = "";
  itemId: string = "";

  itemTypes: any[] = [];
  itemCategory: any[] = [];
  sufix: string = "";

  state: any;

  errorMessages: Message[] = [];

  addForm: UntypedFormGroup = new UntypedFormGroup({
    name: new UntypedFormControl('', [Validators.required, Validators.maxLength(30)]),
    type: new UntypedFormControl('', [Validators.required, fromList([ItemType.AMOUNT, ItemType.UNLIMITED, ItemType.WEIGHT])]),
    category: new UntypedFormControl('', [Validators.required]),
    maxAmount: new UntypedFormControl('', [Validators.required, Validators.min(1)]),
  });

  updateForm: UntypedFormGroup = new UntypedFormGroup({
    currentAmount: new UntypedFormControl('', [Validators.required, Validators.min(1)]),
    maxAmount: new UntypedFormControl('', [Validators.required, Validators.min(1)]),
  });

  constructor(private translate: TranslateService, private collectionSerivce: CollectionService, 
    private messageService: MessageService, private router: Router, private scroller: ViewportScroller,
    private stateService: PanelStateService) {
    this.translate.get('add-collection.locations').subscribe(() => {
      this.itemTypes = [
        {type: this.translate.instant('item.weight'), value: ItemType.WEIGHT},
        {type: this.translate.instant('item.amount'), value: ItemType.AMOUNT},
        {type: this.translate.instant('item.unlimited'), value: ItemType.UNLIMITED},
      ]
      this.itemCategory = [
        {category: this.translate.instant('category.animals'), value: ItemCategory.ANIMALS},
        {category: this.translate.instant('category.children'), value: ItemCategory.CHILDREN},
        {category: this.translate.instant('category.clothes'), value: ItemCategory.CLOTHES},
        {category: this.translate.instant('category.electronic'), value: ItemCategory.ELECTRONIC},
        {category: this.translate.instant('category.food'), value: ItemCategory.FOOD},
        {category: this.translate.instant('category.medicine'), value: ItemCategory.MEDICINE},
        {category: this.translate.instant('category.other'), value: ItemCategory.OTHER},
      ]
    })
    this.addForm.get('maxAmount')?.disable();

    this.state = this.stateService.state$.getValue() || {}
    this.stateService.state$.next("");
    if (Object.keys(this.state).length !== 0) {
      if ("message" in this.state) {
        this.messageService.add({severity:'success', detail: this.state.message});
      }
    }
  }

  ngOnInit(): void {
  }

  ngOnChanges(): void {
    this.items = this.itemsData;
    this.totalRecords = this.items.length;
  }

  addItem() {
    let item = new Item();
    item.name = this.addForm.value.name;
    item.type = this.addForm.value.type;
    item.category = this.addForm.value.category;
    item.maxAmount = this.addForm.value.maxAmount;
    let itemToAdd = this.mapItem(item);
    console.log(itemToAdd);
    this.collectionSerivce.addItem(itemToAdd, this.collectionId.toString()).subscribe({
      next: (data) => {
        this.submitted = true;
        this.itemDialog = false;
        this.table.reset();
        this.addForm.reset();
        this.reloadComponent(this.translate.instant('panel.item_added'));
      },
      error: (error) => {
        console.log(error);
      }
    })
  }

  addNewItemDialog() {
    this.submitted = false;
    this.itemDialog = true;
  }

  editItem(item: Item) {
    this.updateForm.controls['currentAmount'].setValue(item.currentAmount);
    this.updateForm.controls['maxAmount'].setValue(item.maxAmount);
    this.editted = false;
    this.itemUpdateDialog = true;
    this.itemSuffix = this.translate.instant('suffix.' + item.type.toString().toLocaleLowerCase());
    this.itemId = item.id;
  }

  updateItem() {
    let itemUpdate = new ItemUpdate();
    itemUpdate.currentAmount = this.updateForm.value.currentAmount;
    itemUpdate.maxAmount = this.updateForm.value.maxAmount;

    this.collectionSerivce.updateCollectionItem(itemUpdate, this.collectionId.toString(), this.itemId).subscribe({
      next: (data) => {
        this.editted = true;
        this.itemUpdateDialog = false;
        this.updateForm.reset();
        this.reloadComponent(this.translate.instant('panel.item_updated'));
      },
      error: (error) => {
        this.errorMessages = [
          {severity:'error', detail: this.translate.instant(error.error.error)}
        ]
      }
    })
  }

  hideItemDialog() {
    this.itemDialog = false;
    this.submitted = false;
    this.addForm.reset();
  }

  hideUpdateItemDialog() {
    this.itemUpdateDialog = false;
    this.editted = false;
    this.updateForm.reset();
  }

  updateUnlimitedItem(item: Item, endCollect: boolean) {
    let itemUpdate = new ItemUpdate();
    if (endCollect) {
      itemUpdate.currentAmount = item.maxAmount
    } else {
      itemUpdate.currentAmount = 0;
    }
    itemUpdate.maxAmount = item.maxAmount;
    this.collectionSerivce.updateCollectionItem(itemUpdate, this.collectionId.toString(), item.id).subscribe({
      next: (data) => {
        this.messageService.add({severity:'success', detail: this.translate.instant("panel.item_updated")});
        this.reloadComponent(this.translate.instant('panel.item_updated'));
      },
      error: (error) => {
        console.log(error);
      }
    })
  }

  checkValidation(input: string){
    const validation = this.addForm.get(input)?.invalid && (this.addForm.get(input)?.dirty || this.addForm.get(input)?.touched)
    return validation;
  }

  changeItemType(event:any){
    const itemType = event.value
    if (itemType === ItemType.WEIGHT) {
      this.addForm.get('maxAmount')?.enable();
      this.sufix = " kg";
    } else if (itemType === ItemType.AMOUNT) {
      this.addForm.get('maxAmount')?.enable();
      this.sufix = this.translate.instant('item.suffix')
    } else {
      this.addForm.get('maxAmount')?.disable();
      this.sufix = ""
    }
  }

  reloadComponent(message: any) {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    this.router.onSameUrlNavigation = 'reload';
    this.state.message = message
    this.stateService.state$.next(this.state);
    this.router.navigateByUrl('/collections/' + this.collectionId + "/panel");
  }

  scroll(id: string) {
    const element = document.getElementById(id);
    element?.scrollIntoView({behavior: "smooth"})
    this.scroller.scrollToAnchor(id);
  }

  mapItem(tempItem: Item) {
    const item = new ItemToAdd();
    item.name = tempItem.name;
    item.category = Object.keys(ItemCategory)[Object.values(ItemCategory).indexOf(tempItem.category)];
    item.type = Object.keys(ItemType)[Object.values(ItemType).indexOf(tempItem.type)];
    item.maxAmount = tempItem.maxAmount;

    return item;
  }

}

export function fromList(list: any[]): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const valueExists = list.includes(control.value);
    return valueExists ? null : {error: {value: "incorrect enum"}};
  };
}
