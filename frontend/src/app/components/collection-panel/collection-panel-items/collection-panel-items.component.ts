import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, UntypedFormControl, UntypedFormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Item, ItemCategory, ItemType } from 'app/common/itemToAdd';
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

  itemTypes: any[] = [];

  itemCategory: any[] = [];

  sufix: string = "";

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

  constructor(private translate: TranslateService) {
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
  }

  ngOnInit(): void {
  }

  ngOnChanges(): void {
    this.items = this.itemsData;
    this.totalRecords = this.items.length;
  }

  updateItem() {
    // this.item.maxAmount = this.form.value.currentAmount;
    // this.item.maxAmount = this.form.value.maxAmount;

    // this.submitted = true;
    // this.itemDialog = false;
    // this.table.reset();
    // this.addForm.reset();
  }

  addItem() {
    this.submitted = true;
    this.itemDialog = false;
    this.table.reset();
    this.addForm.reset();
  }

  addNewItem() {
    this.submitted = false;
    this.itemDialog = true;
  }

  editItem(index: number) {
    console.log("EDIT");
  }

  deleteItem(index: number) {
    this.items.splice(index, 1);
    this.table.reset();
  }

  hideDialog() {
    this.itemDialog = false;
    this.submitted = false;
    this.addForm.reset();
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

}

export function fromList(list: any[]): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const valueExists = list.includes(control.value);
    return valueExists ? null : {error: {value: "incorrect enum"}};
  };
}
