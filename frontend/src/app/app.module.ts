// ANGULAR STUFF
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { Routes, RouterModule } from '@angular/router';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgxTranslateModule } from './translate/translate.module';
import { NgxPaginationModule } from 'ngx-pagination';
import { ShareButtonsModule } from 'ngx-sharebuttons/buttons';
import { ShareIconsModule } from 'ngx-sharebuttons/icons';

// COMPONENTS
import { AppComponent } from './app.component';
import { HomePageComponent } from './components/home-page/home-page.component';
import { LoginComponent } from './components/login/login.component';
import { SignupComponent } from './components/signup/signup.component';
import { VerificationComponent } from './components/verification/verification.component';
import { CollectionListComponent } from './components/collection-list/collection-list.component';
import { LanguageSelectorComponent } from './components/language-selector/language-selector.component';
import { CollectionInfoComponent } from './components/collection/collection-info/collection-info.component';
import { ItemListComponent } from './components/collection/item-list/item-list.component';
import { LocationListComponent } from './components/collection/location-list/location-list.component';
import { CommentListComponent } from './components/collection/comment-list/comment-list.component';
import { LogoutComponent } from './components/logout/logout.component';
import { LoadingSpinnerComponent } from './components/loading-spinner/loading-spinner.component';
import { AddCollectionComponent } from './components/add/add-collection/add-collection.component';
import { CollectionLocationsComponent } from './components/add/collection-locations/collection-locations.component';
import { CollectionItemsComponent } from './components/add/collection-items/collection-items.component';
import { CollectionDataComponent } from './components/add/collection-data/collection-data.component';
import { OverviewComponent } from './components/collection/overview/overview.component';
import { ProfileComponent } from './components/profile/profile.component';
import { ProfileCollectionsComponent } from './components/profile-collections/profile-collections.component';
import { ProfileDonatesComponent } from './components/profile-donates/profile-donates.component';
import { CollectionPanelInfoComponent } from './components/collection-panel/collection-panel-info/collection-panel-info.component';
import { CollectionPanelItemsComponent } from './components/collection-panel/collection-panel-items/collection-panel-items.component';
import { CollectionPanelLocationsComponent } from './components/collection-panel/collection-panel-locations/collection-panel-locations.component';
import { CollectionPanelCommentsComponent } from './components/collection-panel/collection-panel-comments/collection-panel-comments.component';
import { CollectionPanelSubmittedItemsComponent } from './components/collection-panel/collection-panel-submitted-items/collection-panel-submitted-items.component';
import { VerificationEmailComponent } from './components/verification-email/verification-email.component';

// SERVICES
import { ConfirmationService, MessageService } from 'primeng/api';
import { TokenInterceptorService } from './services/token-interceptor.service';
import { LoadingInterceptorService } from './services/loading-interceptor.service';
import { AuthGuardService } from './services/auth-guard.service';
import { UserOwnerGuardService } from './services/user-owner-guard.service';

// MAT MODULES
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';

// PRIMENG MODULES
import { TableModule } from 'primeng/table';
import { SliderModule } from 'primeng/slider';
import { InputNumberModule } from 'primeng/inputnumber';
import { ProgressBarModule } from 'primeng/progressbar';
import { PaginatorModule } from 'primeng/paginator';
import { ConfirmPopupModule } from 'primeng/confirmpopup';
import { ButtonModule } from 'primeng/button';
import { AvatarModule } from 'primeng/avatar';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { MessagesModule } from 'primeng/messages';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { StepsModule } from 'primeng/steps';
import { FileUploadModule } from 'primeng/fileupload';
import { DialogModule } from 'primeng/dialog';
import { ToolbarModule } from 'primeng/toolbar';
import { SelectButtonModule } from 'primeng/selectbutton';
import { ConfirmDialogModule } from 'primeng/confirmdialog'
import { ToastModule } from 'primeng/toast';
import { DividerModule } from 'primeng/divider';
import { CheckboxModule } from 'primeng/checkbox';
import { DropdownModule } from 'primeng/dropdown';
import { SidebarModule } from 'primeng/sidebar';
import { BadgeModule } from 'primeng/badge';
import { KnobModule } from 'primeng/knob';
import { CarouselModule } from 'primeng/carousel';
import { CollectionClosedComponent } from './components/collection/collection-closed/collection-closed.component';
import { CollectionNotFoundComponent } from './components/collection/collection-not-found/collection-not-found.component';
import { CollectionPanelNoSubmittedItemsComponent } from './components/collection-panel/collection-panel-no-submitted-items/collection-panel-no-submitted-items.component';

const routes: Routes = [
  {path: 'home', component: HomePageComponent},
  {path: 'login', component: LoginComponent},
  {path: 'logout', component: LogoutComponent},
  {path: 'signup', component: SignupComponent},
  {path: 'verify', component: VerificationComponent},
  {path: 'verify/token/:token/user/:username', component: VerificationEmailComponent},
  {path: 'profile', component: ProfileComponent, canActivate:[AuthGuardService]},
  {path: 'collections', component: CollectionListComponent},
  {path: 'collections/:id/panel', component: CollectionPanelInfoComponent, canActivate:[AuthGuardService, UserOwnerGuardService]},
  {path: 'collections/:id/panel/submitted', component: CollectionPanelSubmittedItemsComponent, canActivate:[AuthGuardService, UserOwnerGuardService]},
  {path: 'collections/:id', component: CollectionInfoComponent},
  {path: 'collections/donate/overview', component: OverviewComponent, canActivate:[AuthGuardService]},
  {
    path: 'add-collection',
    canActivate:[AuthGuardService],
    component: AddCollectionComponent,
    children: [
      { path: '', redirectTo: 'info', pathMatch: 'full'},
      { path: 'info', component: CollectionDataComponent },
      { path: 'locations', component: CollectionLocationsComponent },
      { path: 'items', component: CollectionItemsComponent },
    ],
  },
  {path: '', redirectTo: '/home', pathMatch: 'full'},
  {path: '**', redirectTo: '/home', pathMatch: 'full'},
  
];

@NgModule({
  declarations: [
    HomePageComponent,
    AppComponent,
    LoginComponent,
    SignupComponent,
    VerificationComponent,
    CollectionListComponent,
    LanguageSelectorComponent,
    CollectionInfoComponent,
    ItemListComponent,
    CommentListComponent,
    LocationListComponent,
    LogoutComponent,
    LoadingSpinnerComponent,
    AddCollectionComponent,
    CollectionLocationsComponent,
    CollectionItemsComponent,
    CollectionDataComponent,
    OverviewComponent,
    ProfileComponent,
    ProfileCollectionsComponent,
    ProfileDonatesComponent,
    CollectionPanelInfoComponent,
    CollectionPanelItemsComponent,
    CollectionPanelLocationsComponent,
    CollectionPanelCommentsComponent,
    CollectionPanelSubmittedItemsComponent,
    VerificationEmailComponent,
    CollectionClosedComponent,
    CollectionNotFoundComponent,
    CollectionPanelNoSubmittedItemsComponent,
  ],
  imports: [
    RouterModule.forRoot(routes, {scrollPositionRestoration: 'enabled',}),
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    MatInputModule,
    MatCardModule,
    MatIconModule,
    MatButtonToggleModule,
    MatProgressBarModule,
    MatExpansionModule,
    MatPaginatorModule,
    MatSelectModule,
    NgxTranslateModule,
    MatCheckboxModule,
    MatDividerModule,
    MatListModule,
    MatDatepickerModule,
    MatNativeDateModule,
    TableModule,
    SliderModule,
    InputNumberModule,
    ProgressBarModule,
    PaginatorModule,
    ConfirmPopupModule,
    ButtonModule,
    AvatarModule,
    InputTextareaModule,
    MessagesModule,
    ProgressSpinnerModule,
    StepsModule,
    FileUploadModule,
    DialogModule,
    ToolbarModule,
    SelectButtonModule,
    ConfirmDialogModule,
    ToastModule,
    NgxPaginationModule,
    DividerModule,
    CheckboxModule,
    DropdownModule,
    ShareButtonsModule, 
    ShareIconsModule,
    SidebarModule,
    BadgeModule,
    KnobModule,
    CarouselModule
  ],
  providers: [
    ConfirmationService, MessageService,
    {provide: HTTP_INTERCEPTORS, useClass: TokenInterceptorService, multi: true}, 
    {provide: HTTP_INTERCEPTORS, useClass: LoadingInterceptorService, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
