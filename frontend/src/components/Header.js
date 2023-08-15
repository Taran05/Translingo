import React from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { AppBar, Toolbar, Typography } from '@mui/material';

const Header = () => {
  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" style={{ fontWeight: 'bold', marginLeft: '30px', padding: '20px' }}>
          <RouterLink to="/" style={{ textDecoration: 'none', color: 'inherit' }}>
            TRANSLINGO
          </RouterLink>
        </Typography>
      </Toolbar>
    </AppBar>
  );
};

export default Header;
