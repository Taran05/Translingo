import React, { useEffect, useState } from 'react';
import { LinearProgress, Typography } from '@mui/material';
import { useHistory } from 'react-router-dom';
import './TranslationStatus.css';

const TranslationStatus = () => {
  const [progress, setProgress] = useState(0);
  const [isComplete, setIsComplete] = useState(false);
  const history = useHistory();

  useEffect(() => {
    // Simulate progress updates every 2 seconds
    const progressInterval = setInterval(() => {
      if (progress < 100) {
        setProgress(progress + 20);
      } else {
        setIsComplete(true);
        clearInterval(progressInterval);
      }
    }, 2000);

    // Clean up the interval when the component is unmounted
    return () => {
      clearInterval(progressInterval);
    };
  }, [progress]);

  const getMessage = (progress) => {
    switch (progress) {
      case 20:
        return 'Job details enqueued in SQS queue';
      case 40:
        return 'Lambda function triggered';
      case 60:
        return 'Retrieved the document from S3';
      case 80:
        return 'Performed translation using Amazon Translate';
      case 100:
        return 'Stored the translated document in S3';
      default:
        return '';
    }
  };

  useEffect(() => {
    if (isComplete) {
      setTimeout(() => {
        history.push('/downloads');
      }, 3000); // Redirect after 3 seconds
    }
  }, [isComplete, history]);

  return (
    <div style={{ marginTop: '70px', border: '2px solid #ccc', marginLeft: '80px', width: '700px', height: '300px' }}>
      <Typography variant="h6" style={{ fontWeight: 'bold', paddingBottom: '30px', marginLeft: '20px', paddingTop: '20px', fontSize: '24px' }}>Translation Progress:</Typography>
      <LinearProgress variant="determinate" value={progress} style={{ marginLeft: '20px', marginRight: '20px' }}/>
      {progress > 0 && <Typography style={{ paddingTop: '30px', marginLeft: '20px', fontSize: '18px' }}>{getMessage(progress)}</Typography>}
      {isComplete && (
        <>
          <Typography variant="subtitle1" style={{ paddingTop: '20px', paddingBottom: '20px', fontWeight: 'bold', fontSize: '20px', marginLeft: '20px' }} className='download-message'>Your file is ready to be downloaded!</Typography>
        </>
      )}
    </div>
  );
};

export default TranslationStatus;
