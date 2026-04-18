/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'ivory': {
          DEFAULT: '#FDFCF8',
          dark: '#F5F2E9',
        },
        'ink': {
          DEFAULT: '#1A1A1A',
          light: '#333333',
        },
        'gold': {
          DEFAULT: '#C5A059',
          light: '#D4B47B',
        },
        'paper': '#F9F7F2',
      },
      fontFamily: {
        'display': ['"Playfair Display"', 'serif'],
        'sans': ['Geist', 'Inter', 'system-ui', 'sans-serif'],
      },
      boxShadow: {
        'soft': '0 4px 20px -2px rgba(0, 0, 0, 0.05)',
        'inner-soft': 'inset 0 2px 4px 0 rgba(0, 0, 0, 0.03)',
      }
    },
  },
  plugins: [],
}
