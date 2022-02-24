import { triangleOutline, triangleSharp } from 'ionicons/icons';
import React from 'react';
import CreateAndDestroyMapPage from './pages/Map/CreateAndDestroyMap';
import AddAndRemoveMarkers from './pages/Markers/AddAndRemoveMarkers';

export type RouteDescription = {
  title: string;
  url: string;
  iosIcon: string;
  mdIcon: string;
  component: React.FC;
};

export type RouteGroup = {
  groupName: string;
  pages: RouteDescription[];
};

const routesList: RouteGroup[] = [
  {
    groupName: 'Maps',
    pages: [
      {
        title: 'Create and Destroy Maps',
        url: '/maps/create-and-destroy',
        iosIcon: triangleOutline,
        mdIcon: triangleSharp,
        component: CreateAndDestroyMapPage,
      },      
    ],
  },
  {
    groupName: "Markers",
    pages: [
      {
        title: "Add and Remove Marker",
        url: "/markers/add-and-remove",
        iosIcon: triangleOutline,
        mdIcon: triangleSharp,
        component: AddAndRemoveMarkers
      }
    ]
  }
];

export function getRouterSetup(): RouteDescription[] {
  let allPages: RouteDescription[] = [];
  for (const routeGroup of routesList) {
    allPages = [...allPages, ...routeGroup.pages];
  }

  return allPages;
}

export function getMenuList(): RouteGroup[] {
  return routesList;
}
