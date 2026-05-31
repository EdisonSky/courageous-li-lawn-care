import { useState } from 'react';
import { SignupForm } from './components/SignupForm';
import { SignupSuccess } from './components/SignupSuccess';
import type { SignupResult } from './types';
import './App.css';

export default function App() {
  const [result, setResult] = useState<SignupResult | null>(null);

  return (
    <div className="page">
      <header className="hero">
        <p className="eyebrow">CourgiousLi Lawn Care</p>
        <h1>Sign up for lawn service</h1>
        <p className="subtitle">
          Fill out the form below. We create your customer profile and schedule
          your service in one step.
        </p>
      </header>

      <main className="card">
        {result ? (
          <SignupSuccess result={result} onReset={() => setResult(null)} />
        ) : (
          <SignupForm onSuccess={setResult} />
        )}
      </main>
    </div>
  );
}
