import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { FetchPage } from './fetch.page';

const routes: Routes = [
  {
    path: '',
    component: FetchPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FetchPageRoutingModule {}
