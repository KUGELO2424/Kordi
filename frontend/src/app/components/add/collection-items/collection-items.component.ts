import { Component, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, UntypedFormControl, UntypedFormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { ItemCategory, Item, ItemType } from 'app/common/itemToAdd';
import { StateService } from 'app/services/state.service';
import { Table } from 'primeng/table';

@Component({
  selector: 'app-collection-items',
  templateUrl: './collection-items.component.html',
  styleUrls: ['./collection-items.component.css']
})
export class CollectionItemsComponent implements OnInit {

  @ViewChild('dt') table: Table;

  itemDialog: boolean;

  items: Item[] = [];

  item: Item;

  submitted: boolean;

  itemTypes: any[] = [];

  itemCategory: any[] = [];

  sufix: string = "";

  state: any;

  form: UntypedFormGroup = new UntypedFormGroup({
    name: new UntypedFormControl('', [Validators.required, Validators.maxLength(30)]),
    type: new UntypedFormControl('', [Validators.required, fromList([ItemType.AMOUNT, ItemType.UNLIMITED, ItemType.WEIGHT])]),
    category: new UntypedFormControl('', [Validators.required]),
    maxAmount: new UntypedFormControl('', [Validators.required, Validators.min(1)]),
  });

  constructor(private stateService: StateService, private translate: TranslateService) {
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
    this.form.get('maxAmount')?.disable();
  }

  ngOnInit(): void {
    this.state = this.stateService.state$.getValue() || {}
    if (Object.keys(this.state).length === 0) {
      return;
    }
    if ("items" in this.state && this.state.items.length !== 0) {
      this.items = this.state.items;
    }
  }

  saveState() {
    this.state.items = this.items
    this.stateService.state$.next(this.state); 
  }

  addNewItem() {
    this.submitted = false;
    this.itemDialog = true;
  }

  saveItem() {
    this.item = new Item();
    this.item.id = this.createId();
    this.item.name = this.form.value.name;
    this.item.type = this.form.value.type;
    this.item.category = this.form.value.category;
    this.item.maxAmount = this.form.value.maxAmount;
    this.submitted = true;
    this.items.push(this.item);
    this.itemDialog = false;
    this.table.reset();
    this.form.reset();
  }

  deleteItem(index: number) {
    this.items.splice(index, 1);
    this.table.reset();
  }
  
  hideDialog() {
    this.itemDialog = false;
    this.submitted = false;
    this.form.reset();
  }

  createId(): string {
    let id = '';
    var chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    for ( var i = 0; i < 5; i++ ) {
        id += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return id;
  }

  checkValidation(input: string){
    const validation = this.form.get(input)?.invalid && (this.form.get(input)?.dirty || this.form.get(input)?.touched)
    return validation;
  }

  changeItemType(event:any){
    const itemType = event.value
    if (itemType === ItemType.WEIGHT) {
      this.form.get('maxAmount')?.enable();
      this.sufix = " kg";
    } else if (itemType === ItemType.AMOUNT) {
      this.form.get('maxAmount')?.enable();
      this.sufix = this.translate.instant('item.suffix')
    } else {
      this.form.get('maxAmount')?.disable();
      this.sufix = ""
    }
  }

}

export function fromList(list: any[]): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const valueExists = list.includes(control.value);
    return valueExists ? null : {error: {value: "incorrect enum"}};
  };
}
