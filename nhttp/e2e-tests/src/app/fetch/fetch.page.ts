import { Component, OnInit } from '@angular/core';
import { PatchAPICalls } from '@capacitor/nhttp';

@Component({
  selector: 'app-fetch',
  templateUrl: './fetch.page.html',
  styleUrls: ['./fetch.page.scss'],
})
export class FetchPage implements OnInit {

  books = [];

  constructor() { }

  ngOnInit() {
    PatchAPICalls();
  }

  async getBooks() {
    const response = await fetch(
      'https://www.anapioficeandfire.com/api/books',
      {headers: {'Content-Type': 'application/json'}, method: "GET"}
    );

    const json = await response.json();
    console.log(json);
    this.books = json;
  }

}
