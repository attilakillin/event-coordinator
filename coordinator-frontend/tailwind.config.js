const colors = require('tailwindcss/colors');

/** @type {import('tailwindcss').Config} */
module.exports = {
    content: ["./src/**/*.{ts,tsx}"],
    theme: {
        colors: {
            theme: colors.slate,
            red: colors.red
        },
        extend: {},
    },
    plugins: [],
}
