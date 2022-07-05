/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/* eslint-disable no-undef */
module.exports = {
  content: ['./src/**/*.html', './src/**/*.scss'],
  theme: {
    extend: {
      fontFamily: {
        black: ['Catena-X Black', 'sans-serif'],
        blackItalic: ['Catena-X BlackItalic', 'sans-serif'],
        bold: ['Catena-X Bold', 'sans-serif'],
        boldItalic: ['Catena-X BoldItalic', 'sans-serif'],
        extraBold: ['Catena-X ExtraBold', 'sans-serif'],
        extraBoldItalic: ['Catena-X ExtraBoldItalic', 'sans-serif'],
        extraLight: ['Catena-X ExtraLight', 'sans-serif'],
        extraLightItalic: ['Catena-X ExtraLightItalic', 'sans-serif'],
        italic: ['Catena-X Italic', 'sans-serif'],
        light: ['Catena-X Light', 'sans-serif'],
        lightItalic: ['Catena-X LightItalic', 'sans-serif'],
        medium: ['Catena-X Medium', 'sans-serif'],
        mediumItalic: ['Catena-X MediumItalic', 'sans-serif'],
        regular: ['Catena-X Regular', 'sans-serif'],
        semiBold: ['Catena-X SemiBold', 'sans-serif'],
        semiBoldItalic: ['Catena-X SemiBoldItalic', 'sans-serif'],
        thin: ['Catena-X Thin', 'sans-serif'],
        thinItalic: ['Catena-X ThinItalic', 'sans-serif'],
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
        tiny: '.5rem',
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
      primary: '#ffa600',
      secondary: '#b3cb2d',

      primaryLight: '#ffd74a',
      primaryDark: '#c67700',

      secondaryLight: '#e8fe63',
      secondaryDark: '#809a00',

      danger: '#D91E18',
      dangerLight: '#E5231D',

      interactive: '#046B99',
      interactiveLight: '#017AB0',

      // Basic colors
      cararra: '#eeefea',
      doveGray: '#666666',
      tundora: '#444444',
      white: '#fff',
      dark: '#191715',

      // Alert colors
      error: '#D91E18',
      success: '#3db014',
      warning: '#ffad1f',
      alert: '#fe6702',
      info: '#ffa600',
      inactive: '#D8D8D8',

      // Basic gray accent color
      tundoraShadeGray: '#8f8f8f',
      tundoraShadeNobel: '#b4b4b4',
      tundoraShadeAlto: '#dadada',
      accordionGray: '#fafafa',

      // Gray accent color
      dustyGray: '#999999',
      dustyGrayShadeAlto: '#d9d9d9',
      dustyGrayShadeGallery: '#efefef',
      dustyGrayShadeWildSand: '#f6f6f6',

      // Dark blue primary accent color
      blueBayoux: '#fc8a00',
      blueBayouxShadeWaikawaGray: '#ff9600',
      blueBayouxShadeLynch: '#ffa600',
      blueBayouxShadePigeonPost: '#ffb640',

      selectionBlue: '#e9ebf1',
      investigationBlue: '#3f7bfe',

      footer: '#fff5cc',
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
