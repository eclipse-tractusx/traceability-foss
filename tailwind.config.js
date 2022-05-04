/*
 * Copyright 2021 The PartChain Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* eslint-disable no-undef */
module.exports = {
  purge: {
    enabled: true,
    content: ['./src/**/*.html', './src/**/*.scss'],
    important: true,
  },
  darkMode: false, // or 'media' or 'class'
  theme: {
    extend: {
      fontFamily: {
        bold: ['PartChain Bold', 'sans-serif'],
        boldItalic: ['PartChain Bold Italic', 'sans-serif'],
        boldCondensed: ['PartChain Bold Condensed', 'sans-serif'],
        boldCondensedItalic: ['PartChain Bold Italic Condensed', 'sans-serif'],
        condensedRegular: ['PartChain Regular Condensed', 'sans-serif'],
        condensedItalic: ['PartChain Italic Condensed', 'sans-serif'],
        lightCondensedRegular: ['PartChain Light Condensed', 'sans-serif'],
        lightCondensedItalic: ['PartChain Light Italic Condensed', 'sans-serif'],
        italic: ['PartChain Italic', 'sans-serif'],
        regular: ['PartChain Regular', 'sans-serif'],
        light: ['PartChain Light', 'sans-serif'],
        lightItalic: ['PartChain LightItalic', 'sans-serif'],
      },
      minHeight: {
        '0': '0',
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
        '120': 120,
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
      primary: '#5b708f',
      secondary: '#fff',

      // Basic colors
      cararra: '#eeefea',
      doveGray: '#666666',
      tundora: '#444444',
      white: '#fff',
      dark: '#000',

      // Alert colors
      error: '#d20000',
      success: '#3db014',
      warning: '#ffad1f',
      alert: '#fe6702',
      info: '#5b708f',
      inactive: '#D8D8D8',
      danger: '#610e4a',

      // Highlight colors
      nepal: '#92a2bd',
      nepalShadeHeather: '#bdc6d6',
      nepalShadeGeyser: '#d2dae3',
      nepalShadeGeyserLighter: '#e9ebf1',

      // Yellow accent colors
      supernova: '#fecb00',
      supernovaShadeGoldenrod: '#fddf66',
      supernovaShadeCreamBrulee: '#ffea99',
      supernovaShadeBarleyWhite: '#fff5cc',

      // Basic gray accent color
      tundoraShadeGray: '#8f8f8f',
      tundoraShadeNobel: '#b4b4b4',
      tundoraShadeAlto: '#dadada',
      accordionGray: '#fafafa',

      // Blue accent color
      dodgerBlue: '#3f7bfe',
      dodgerBlueShadeMalibu: '#6595fd',
      dodgerBlueShadeMalibuLighter: '#8cb0fe',
      dodgerBlueShadeMeirose: '#b3caff',
      dodgerBlueShadePattensBlue: '#d9e5ff',

      // Green accent color
      killarney: '#3e6a3d',
      killarneyShadeGladeGreen: '#658763',
      killarneyShadeEnvy: '#8ba68a',
      killarneyShadeGreenSpring: '#b0c3b0',
      killarneyShadeTasman: '#d8e1d8',

      // Red accent color
      darkBurgundy: '#7c0b0e',
      darkBurgundyShadeTabasco: '#b21014',
      darkBurgundyShadeNewYorkPink: '#d16f71',
      darkBurgundyShadePetiteOrchid: '#de9a9b',
      darkBurgundyShadeRoseFog: '#eabebf',

      // Gray accent color
      dustyGray: '#999999',
      dustyGrayShadeAlto: '#d9d9d9',
      dustyGrayShadeGallery: '#efefef',
      dustyGrayShadeWildSand: '#f6f6f6',

      // Dark blue primary accent color
      blueBayoux: '#526482',
      blueBayouxShadeWaikawaGray: '#5b708f',
      blueBayouxShadeLynch: '#617899',
      blueBayouxShadePigeonPost: '#acbcd7',

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
      '1': '1 1 0%',
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
