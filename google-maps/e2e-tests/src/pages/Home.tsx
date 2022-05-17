import BaseTestingPage from '../components/BaseTestingPage';

const Home: React.FC = () => {
  return (
    <BaseTestingPage pageTitle="Test App">
      <h3 className="ion-padding" style={{ textAlign: 'center' }}>
        Choose a testing page from the menu to get started!
      </h3>
    </BaseTestingPage>
  );
};

export default Home;
