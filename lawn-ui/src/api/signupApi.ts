import type { SignupInput, SignupResult } from '../types';

const API_BASE = import.meta.env.VITE_API_BASE ?? '';

export async function createSignup(payload: SignupInput): Promise<SignupResult> {
  const response = await fetch(`${API_BASE}/api/signups`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `Request failed (${response.status})`);
  }

  return response.json() as Promise<SignupResult>;
}
