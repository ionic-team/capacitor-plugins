export default class Page {
  public async hideToolBars() {
    await driver.execute(() => {
      for (let i = 0; i < document.getElementsByTagName('ion-toolbar').length; i++) {
        const toolbar: HTMLElement | null = document.getElementsByTagName('ion-toolbar').item(i) as HTMLElement;
        if (toolbar !== null) {
          toolbar.style.display = 'none';
        }
      }
    });
  }

  public async showToolBars() {
    await driver.execute(() => {
      for (let i = 0; i < document.getElementsByTagName('ion-toolbar').length; i++) {
        const toolbar: HTMLElement | null = document.getElementsByTagName('ion-toolbar').item(i) as HTMLElement;
        if (toolbar !== null) {
          toolbar.style.display = '';
        }
      }
    });
  }
}
