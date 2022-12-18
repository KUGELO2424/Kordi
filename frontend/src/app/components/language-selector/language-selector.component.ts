import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-language-selector',
  templateUrl: './language-selector.component.html',
  styleUrls: ['./language-selector.component.css']
})
export class LanguageSelectorComponent implements OnInit {

  default_lang = 'pl';

  readonly languages = [
    { value: 'en', label: 'EN', img: 'assets/img/flags/uk.png' }, 
    { value: 'pl', label: 'PL', img: 'assets/img/flags/pl.png'}
  ];

  public language = this.languages[1];

  constructor(private translate: TranslateService) { }

  ngOnInit(): void {
    var lang: string = localStorage.getItem('lang')!;
    if (lang != undefined && lang != null) {
      this.selectLanguage(lang)
    } else {
      this.selectLanguage(this.default_lang);
    }
  }

  selectLanguage(value: string) {
    this.language = this.languages.find( lang => lang.value === value)!;
    this.translate.use(value);
    localStorage.setItem('lang', value)
    const currentLanguage = this.translate.currentLang;
  }

}
