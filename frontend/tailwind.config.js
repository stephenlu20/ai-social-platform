
/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'veritas-pink': '#ff6b9d',
        'veritas-pink-dark': '#c44569',
        'veritas-coral': '#ffa07a',
        'veritas-purple': '#8a2be2',
        'veritas-purple-light': '#b794f6',
        'veritas-blue': '#667eea',
        'veritas-blue-dark': '#764ba2',
      },
      fontFamily: {
        'sans': ['Plus Jakarta Sans', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [],
}

