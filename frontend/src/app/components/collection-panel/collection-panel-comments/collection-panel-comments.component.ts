import { ViewportScroller } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CollectionService } from 'app/services/collection.service';
import { MessageService } from 'primeng/api';
import { Comment } from 'app/common/comment';
import { Router } from '@angular/router';
import { PanelStateService } from 'app/services/panel-state.service';

@Component({
  selector: 'app-collection-panel-comments',
  templateUrl: './collection-panel-comments.component.html',
  styleUrls: ['./collection-panel-comments.component.css']
})
export class CollectionPanelCommentsComponent implements OnInit {

  @Input() collectionId: number;

  comments: Comment[];

  page: number = 0;
  pageSize: number = 10;
  totalRecords: number = 10;

  state: any;

  constructor(private collectionService: CollectionService, private scroller: ViewportScroller, 
    private messageService: MessageService, private translate: TranslateService, private router: Router,
    private stateService: PanelStateService) {
      this.state = this.stateService.state$.getValue() || {}
    this.stateService.state$.next("");
    if (Object.keys(this.state).length !== 0) {
      if ("message" in this.state) {
        this.messageService.add({severity:'success', detail: this.state.message});
      }
    }
  }

    ngOnInit(): void {
      this.searchComments();
    }

    searchComments() {
      this.collectionService.getCommentsFromCollection(this.collectionId.toString(), this.page, this.pageSize).subscribe({
        next: (data) => {   
          this.comments = data.content;
          this.page = data.number;
          this.totalRecords = data.totalElements;
        },
        error: (error) => {
          console.log(error);
        }
      })
    }

    deleteComment(comment: Comment) {
      this.collectionService.deleteComment(this.collectionId.toString(), comment.id.toString()).subscribe({
        next: (data) => {
          this.reloadComponent(this.translate.instant('panel.comment_deleted'));
        },
        error: (error) => {
          console.log(error);
        }
      })
    }

    reloadComponent(message: any) {
      this.router.routeReuseStrategy.shouldReuseRoute = () => false;
      this.router.onSameUrlNavigation = 'reload';
      this.state.message = message
      this.stateService.state$.next(this.state);
      this.router.navigateByUrl('/collections/' + this.collectionId + "/panel");
    }

    changePage(event: any) {
      // Scroll if page has been changed
      if (event.rows == this.pageSize && event.page != this.page) {
        this.scroll();
      }
      this.pageSize = event.rows;
      this.page = event.page;
      this.searchComments();
    }

    scroll() {
      this.scroller.scrollToAnchor("commentList");
    }
  

}
