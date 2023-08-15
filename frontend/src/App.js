import React, { useState } from 'react';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';
import { Container, Grid } from '@mui/material';
import Header from './components/Header';
import DocumentUploadForm from './components/DocumentUploadForm';
import DocumentList from './components/DocumentList';
import TranslationStatus from './components/TranslationStatus';
import TranslatedDocumentList from './components/TranslatedDocumentList';
import TranslationForm from './components/TranslationForm';

const App = () => {
  const [uploadTrigger, setUploadTrigger] = useState(false);

  const handleUpload = () => {
    setUploadTrigger(!uploadTrigger);
  };

  return (
    <Router>
      <div>
        <Header />
        <Container maxWidth="md" style={{ marginTop: '2rem', marginBottom: '2rem' }}>
          <Grid container spacing={2}>
            <Grid item xs={12} md={6}>
              <Switch>
                <Route exact path="/">
                  <DocumentUploadForm onUpload={handleUpload} />
                </Route>
              </Switch>
            </Grid>
            <Grid item xs={12} md={6}>
              <Switch>
                <Route exact path="/">
                  <DocumentList key={uploadTrigger} />
                </Route>
              </Switch>
            </Grid>
            <Route exact path="/translations">
              <TranslationStatus />
            </Route>
            <Route exact path="/downloads">
              <TranslatedDocumentList />
            </Route>
            <Route path="/translate" render={({ location }) => <TranslationForm documentId={location.state.documentId} />} />
          </Grid>
        </Container>
      </div>
    </Router>
  );
};

export default App;
