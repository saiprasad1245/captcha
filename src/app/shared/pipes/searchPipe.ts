import { NgModule, Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'searchPipe'
})
export class SearchPipe implements PipeTransform {

    transform(items: any[], searchText: string): any[] {
        //console.log("testPipe",searchText,items)
        if (!items) return [];

        if (!searchText) return items;

        if (searchText && searchText.trim().length) {
            return items.filter(obj => 
                obj.managerName ? obj.managerName.toLowerCase().includes(searchText.toLowerCase())  : obj.toLowerCase().includes(searchText.toLowerCase()))
        }
    }
}

@NgModule({
    imports: [
      // dep modules
    ],
    declarations: [ 
        SearchPipe
    ],
    exports: [
        SearchPipe
    ]
  })
  export class SearchPipeModule {}