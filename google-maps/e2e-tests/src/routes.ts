import { triangleOutline, triangleSharp } from 'ionicons/icons';
import React from 'react';
import PolylineMapPage from './pages/Drawing/Polylines';
import BoundsMapPage from './pages/Map/Bounds';
import ConfigMapPage from './pages/Map/ConfigMap';
import CreateAndDestroyMapPage from './pages/Map/CreateAndDestroyMap';
import AddAndRemoveMarkers from './pages/Markers/AddAndRemoveMarkers';
import MarkerCustomizations from './pages/Markers/MarkerCustomizations';
import MultipleMarkers from './pages/Markers/MultipleMarkers';
import SimpleScrollingPage from './pages/Scrolling/SimpleScrolling';
import PolygonMapPage from './pages/Drawing/Polygons';
import CircleMapPage from './pages/Drawing/Circles';

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
      {
        title: 'Config Maps',
        url: '/maps/config',
        iosIcon: triangleOutline,
        mdIcon: triangleSharp,
        component: ConfigMapPage,
      },
      {
        title: 'Bounds',
        url: '/maps/bounds',
        iosIcon: triangleOutline,
        mdIcon: triangleSharp,
        component: BoundsMapPage,
      },
      {
        title: 'Simple Scrolling',
        url: '/maps/scrolling',
        iosIcon: triangleOutline,
        mdIcon: triangleSharp,
        component: SimpleScrollingPage,
      },
    ],
  },
  {
    groupName: 'Markers',
    pages: [
      {
        title: 'Add and Remove Marker',
        url: '/markers/add-and-remove',
        iosIcon: triangleOutline,
        mdIcon: triangleSharp,
        component: AddAndRemoveMarkers,
      },
      {
        title: 'Multiple Markers',
        url: '/markers/multiple-markers',
        iosIcon: triangleOutline,
        mdIcon: triangleSharp,
        component: MultipleMarkers,
      },
      {
        title: 'Marker Customization',
        url: '/markers/customization',
        iosIcon: triangleOutline,
        mdIcon: triangleSharp,
        component: MarkerCustomizations,
      }
    ]
  },
  {
    groupName: "Drawing",
    pages: [
      {
        title: "Polygons",
        url: "/drawing/polygons",
        iosIcon: triangleOutline,
        mdIcon: triangleSharp,
        component: PolygonMapPage,
      },
      {
        title: "Circles",
        url: "/drawing/circles",
        iosIcon: triangleOutline,
        mdIcon: triangleSharp,
        component: CircleMapPage,
      }, {
        title: 'Polylines',
        url: '/drawing/polylines',
        iosIcon: triangleOutline,
        mdIcon: triangleSharp,
        component: PolylineMapPage,
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
