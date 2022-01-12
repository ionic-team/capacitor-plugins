import { triangleOutline, triangleSharp } from 'ionicons/icons';
import React from 'react';
import BasicEchoRunBasicEchoPage from './pages/BasicEcho/RunBasicEcho';

export type RouteDiscription = {
  title: string;
  url: string;
  iosIcon: string;
  mdIcon: string;
  component: React.FC;
};

export type RouteGroup = {
  groupName: string;
  pages: RouteDiscription[];
};

const routesList: RouteGroup[] = [
  {
    groupName: 'Basic Echo',
    pages: [
      {
        title: 'Run Basic Echo',
        url: '/basic-echo/run-basic-echo',
        iosIcon: triangleOutline,
        mdIcon: triangleSharp,
        component: BasicEchoRunBasicEchoPage,
      },
    ],
  },
];

export function getRouterSetup(): RouteDiscription[] {
  let allPages: RouteDiscription[] = [];
  for (const routeGroup of routesList) {
    allPages = [...allPages, ...routeGroup.pages];
  }

  return allPages;
}

export function getMenuList(): RouteGroup[] {
  return routesList;
}
