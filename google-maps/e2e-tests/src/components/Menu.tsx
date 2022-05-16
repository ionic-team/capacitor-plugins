import { IonContent, IonIcon, IonItem, IonLabel, IonList, IonListHeader, IonMenu, IonMenuToggle } from '@ionic/react';
import { useLocation } from 'react-router-dom';
import './Menu.css';
import { useEffect, useState } from 'react';
import { getMenuList, RouteGroup } from '../routes';
import { homeOutline } from 'ionicons/icons';

const Menu: React.FC = () => {
  const location = useLocation();
  const [menu, setMenu] = useState<RouteGroup[]>([]);

  useEffect(() => {
    setMenu(getMenuList());
  }, []);

  return (
    <IonMenu contentId="main" type="overlay">
      <IonContent>
        <IonList>
          <IonMenuToggle autoHide={false}>
            <IonItem
              className={location.pathname === '/home' ? 'selected' : ''}
              routerLink="/home"
              routerDirection="none"
              lines="none"
              detail={false}
            >
              <IonIcon slot="start" ios={homeOutline} md={homeOutline} />
              <IonLabel>Home</IonLabel>
            </IonItem>
          </IonMenuToggle>
        </IonList>
        {menu.map((routeGroup, indx) => {
          return (
            <IonList key={indx}>
              <IonListHeader>{routeGroup.groupName}</IonListHeader>
              {routeGroup.pages.map((routeDescription, index) => {
                return (
                  <IonMenuToggle key={index} autoHide={false}>
                    <IonItem
                      className={location.pathname === routeDescription.url ? 'selected' : ''}
                      routerLink={routeDescription.url}
                      routerDirection="none"
                      lines="none"
                      detail={false}
                    >
                      <IonIcon slot="start" ios={routeDescription.iosIcon} md={routeDescription.mdIcon} />
                      <IonLabel>{routeDescription.title}</IonLabel>
                    </IonItem>
                  </IonMenuToggle>
                );
              })}
            </IonList>
          );
        })}
      </IonContent>
    </IonMenu>
  );
};

export default Menu;
