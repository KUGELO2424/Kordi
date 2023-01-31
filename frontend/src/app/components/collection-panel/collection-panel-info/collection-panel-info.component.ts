import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Collection } from 'app/common/collection';
import { SubmittedItem } from 'app/common/submittedItem';
import { CollectionService } from 'app/services/collection.service';
import { Location } from '@angular/common'
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { UpdateCollection } from 'app/common/updateCollection';
import { ConfirmationService, MessageService } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-collection-panel-info',
  templateUrl: './collection-panel-info.component.html',
  styleUrls: ['./collection-panel-info.component.css']
})
export class CollectionPanelInfoComponent implements OnInit {

  collection: Collection | undefined;
  submittedItems: SubmittedItem[] = [];
  collectionProgress: number = 0;
  currentDate = new Date(); 
  
  form: UntypedFormGroup = new UntypedFormGroup({
    title: new UntypedFormControl('', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]),
    description: new UntypedFormControl('', [Validators.required, Validators.minLength(3), Validators.maxLength(500)]),
    endDate: new UntypedFormControl(''),
  });

  constructor(private route: ActivatedRoute, private collectionService: CollectionService, private location: Location,
    private messageService: MessageService,  private translate: TranslateService, private confirmationService: ConfirmationService) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(() => {
      this.handleCollectionDetails();
    })
  }

  handleCollectionDetails() {
    const collectionId: string = this.route.snapshot.paramMap.get('id')!;
    this.collectionService.getCollectionById(collectionId).subscribe({
      next: (data) => {   
        this.collection = data;
        this.form.controls['title'].setValue(this.collection.title);
        this.form.controls['description'].setValue(this.collection.description);
        this.form.controls['endDate'].setValue(this.collection.endTime);
        this.handleSubmittedItemsDetails(collectionId)
        this.setCollectionProgress();
      },
      error: () => {

      }
    })
  }

  handleSubmittedItemsDetails(id: string) {
    this.collectionService.getSubmittedItemsFromCollection(id).subscribe({
      next: (data) => {
        this.submittedItems = data;
      }
    })
  }

  confirm(event: Event) {
    this.confirmationService.confirm({
        target: event.target ? event.target : undefined,
        acceptLabel: this.translate.instant("add-collection.yes"),
        rejectLabel: this.translate.instant("add-collection.no"),
        message: this.translate.instant("panel.update_confirm"),
        icon: 'pi pi-exclamation-triangle',
        accept: () => {
          let collectionUpdate = new UpdateCollection();
          collectionUpdate.id = this.collection!.id
          collectionUpdate.title = this.form.value.title;
          collectionUpdate.description = this.form.value.description;
          collectionUpdate.endTime = this.form.value.endDate;
          this.collectionService.updateCollection(collectionUpdate).subscribe({
            next: (data) => {
              this.messageService.add({severity:'success', detail: this.translate.instant("panel.collection_updated")});
            },
            error: (error) => {
              console.log(error);
            }
          })
        },
        reject: () => {
        }
    });
  }

  setCollectionProgress() {
    this.collectionService.getCollectionProgress(this.collection!.items).subscribe({
      next: (data) => {
        this.collectionProgress = data;
      }
    })
  }

  back() {
    this.location.back();
  }

  checkValidation(input: string){
    const validation = this.form.get(input)?.invalid && (this.form.get(input)?.dirty || this.form.get(input)?.touched)
    return validation;
  }

}
