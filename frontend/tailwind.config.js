/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

/* eslint-disable no-undef */
module.exports = {
  content: ['./src/**/*.html', './src/**/*.scss'],
  theme: {
    extend: {
      fontFamily: {
        bold: ['Cofinity-X Bold', 'sans-serif'],
        boldItalic: ['Cofinity-X BoldItalic', 'sans-serif'],

        italic: ['Cofinity-X Italic', 'sans-serif'],
        regular: ['Cofinity-X Regular', 'sans-serif'],

        light: ['Cofinity-X Light', 'sans-serif'],
        lightItalic: ['Cofinity-X LightItalic', 'sans-serif'],

        medium: ['Cofinity-X Medium', 'sans-serif'],
        mediumItalic: ['Cofinity-X MediumItalic', 'sans-serif'],

        regular: ['Cofinity-X Regular', 'sans-serif'],
        regularItalic: ['Cofinity-X Italic', 'sans-serif'],

        semiBold: ['Cofinity-X SemiBold', 'sans-serif'],
        semiBoldItalic: ['Cofinity-X SemiBoldItalic', 'sans-serif'],
      },
      minHeight: {
        0: '0',
        '1/4': '25%',
        '1/2': '50%',
        '3/4': '75%',
        full: '100%',
      },
      screens: {
        '3xl': '1600px',
        '4xl': '1920px',
      },
      spacing: {
        xsm: '.5rem',
        xxl: '3rem',
      },
      zIndex: {
        120: 120,
      },
      fontSize: {
        tiny: '12px',
        sm: '16px',
        base: '18px',
        xl: '20px',
      },
      fill: theme => ({
        green: theme('success'),
        red: theme('error'),
        blue: theme('info'),
        orange: theme('warning'),
        gray: theme('inactive'),
      }),
      maxWidth: {
        '8xl': '90rem',
      },
    },
    colors: {
      // Theme colors
      primary: '#D31184',
      secondary: '#4D4D4D',
      tertiary: '#F58220',

      primaryLight: '#EA1392',
      primaryDark: '#A60F68',

      secondaryLight: '#808080',

      secondaryButton: '#000000',
      secondaryButton: '#333333',

      tertiaryLight: '#F58220',
      tertiaryDark: '#B62100',

      danger: '#B62100',

      dangerLight: '#E5231D',

      interactive: '#0F71CB',
      interactiveLight: '#EAF1FE',

      // Basic colors
      cararra: '#eeefea',
      doveGray: '#666666',
      tundora: '#444444',
      white: '#ffffff',
      dark: '#111111',
      gray: '#888888',

      // Alert colors
      error: '#B62100',
      success: '#00AA55',
      warning: '#F2BA00',
      alert: '#fe6702',
      info: '#046B99',
      inactive: '#D8D8D8',

      // Basic gray accent color
      tundoraShadeGray: '#8f8f8f',
      tundoraShadeNobel: '#b4b4b4',
      tundoraShadeAlto: '#dadada',
      accordionGray: '#fafafa',

      // Surface colors
      surfaceBase: '#FFFFFF',
      surfaceBright: '#FAFAFA',
      surfaceMedium: '#EDEDED',
      surfaceDim: '#DFDFDF',

      inactiveText: '#A6A6A6',
      inactiveShape: '#D2D2D2',

      positive: '#6BA13C',

      // Gray accent color
      dustyGray: '#999999',
      dustyGrayShadeAlto: '#d9d9d9',
      dustyGrayShadeGallery: '#efefef',
      dustyGrayShadeWildSand: '#F2F3FB',

      // Dark blue primary accent color
      blueBayoux: '#526482',
      blueBayouxShadeWaikawaGray: '#5b708f',
      blueBayouxShadeLynch: '#617899',
      blueBayouxShadePigeonPost: '#acbcd7',

      selectionBlue: '#e9ebf1',
      investigationBlue: '#3f7bfe',

      footer: '#fff5cc',

      textLink: '#2484C6',

      // Status colors for notification badges

      createdLight: '#f2f3fb',
      createdDark: '#111111',

      receivedLight: '#e1f1fe',
      receivedDark: '#2b4078',

      pendingLight: '#FDB913',
      pendingDark: '#262124',

      confirmedLight: '#e2f6c7',
      confirmedDark: '#5c8d46',

      declinedLight: '#fee7e2',
      declinedDark: '#ff5330',

      closedLight: '#ffffff',
      closedDark: '#4D4D4D',

      canceledLight: '#ffffff',
      canceledDark: '#524FA1',

      requestedLight: '#ffffff',
      requestedDark: '#A6CE39',

      white: '#ffffff',

      qualityTypeOk: '#3db014',
      qualityTypeMinor: '#ffd74a',
      qualityTypeMajor: '#c67700',
      qualityTypeCritical: '#981b5e',
      qualityTypeLifeThreatening: '#E5231D',

      severityMinor: '#ffd74a',
      severityMajor: '#c67700',
      severityCritical: '#981b5e',
      severityLifeThreatening: '#E5231D',

      semanticDataModelSerialPart: '#3db014',
      semanticDataModelBatch: '#ffd21d',
    },
    screens: {
      sm: '640px',
      md: '768px',
      lg: '1024px',
      xl: '1280px',
      '2xl': '1536px',
    },
    spacing: {
      sm: '1rem',
      md: '1.5rem',
      lg: '2rem',
      xl: '2.5rem',
      px: '1px',
      0: '0',
      0.5: '0.125rem',
      1: '0.25rem',
      1.5: '0.375rem',
      2: '0.5rem',
      2.5: '0.625rem',
      3: '0.75rem',
      3.5: '0.875rem',
      4: '1rem',
      5: '1.25rem',
      6: '1.5rem',
      7: '1.75rem',
      8: '2rem',
      9: '2.25rem',
      10: '2.5rem',
      11: '2.75rem',
      12: '3rem',
      14: '3.5rem',
      16: '4rem',
      20: '5rem',
      24: '6rem',
      28: '7rem',
      32: '8rem',
      36: '9rem',
      40: '10rem',
      44: '11rem',
      48: '12rem',
      52: '13rem',
      56: '14rem',
      60: '15rem',
      64: '16rem',
      72: '18rem',
      80: '20rem',
      96: '24rem',
      navbarLeft: '50px',
      sidebar: '240px',
    },
    flex: {
      1: '1 1 0%',
      auto: '1 1 auto',
      initial: '0 1 auto',
      none: 'none',
    },
  },
  variants: {
    extend: {},
  },
  plugins: [],
};
