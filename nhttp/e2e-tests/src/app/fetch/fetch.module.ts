import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { FetchPageRoutingModule } from './fetch-routing.module';

import { FetchPage } from './fetch.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    FetchPageRoutingModule
  ],
  declarations: [FetchPage]
})
export class FetchPageModule {}
