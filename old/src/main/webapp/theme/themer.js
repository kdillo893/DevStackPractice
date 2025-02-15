'use client';

import { ThemeProvider, createTheme } from "@mui/material";

const darkTheme = createTheme({
  palette: { mode: 'dark', }
});

export default function Themer({ children }) {

  return (
    <ThemeProvider theme={darkTheme}>
      {children}
    </ThemeProvider>
  );
}
