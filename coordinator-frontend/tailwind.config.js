const colors = require('tailwindcss/colors');

/** @type {import('tailwindcss').Config} */
module.exports = {
    content: ["./src/**/*.{ts,tsx}"],
    theme: {
        colors: {
            theme: colors.slate
        },
        extend: {},
    },
    plugins: [],
}
