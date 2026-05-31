import { useState } from 'react';
import { createSignup } from '../api/signupApi';
import {
  emptySignup,
  SERVICE_OPTIONS,
  type CustomerInput,
  type SignupInput,
  type SignupResult,
} from '../types';

interface SignupFormProps {
  onSuccess: (result: SignupResult) => void;
}

function Field({
  label,
  id,
  value,
  onChange,
  type = 'text',
  required = true,
}: {
  label: string;
  id: string;
  value: string | number;
  onChange: (value: string) => void;
  type?: string;
  required?: boolean;
}) {
  return (
    <label className="field" htmlFor={id}>
      <span>{label}</span>
      <input
        id={id}
        type={type}
        value={value}
        required={required}
        onChange={(e) => onChange(e.target.value)}
      />
    </label>
  );
}

function updateCustomer(
  form: SignupInput,
  field: keyof CustomerInput,
  value: string,
): SignupInput {
  return {
    ...form,
    customer: { ...form.customer, [field]: value },
  };
}

export function SignupForm({ onSuccess }: SignupFormProps) {
  const [form, setForm] = useState<SignupInput>(emptySignup);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      const result = await createSignup(form);
      onSuccess(result);
      setForm(emptySignup());
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Something went wrong');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form className="signup-form" onSubmit={handleSubmit}>
      <section className="form-section">
        <h2>Your information</h2>
        <div className="field-grid">
          <Field
            label="Full name"
            id="fullName"
            value={form.customer.fullName}
            onChange={(v) => setForm((f) => updateCustomer(f, 'fullName', v))}
          />
          <Field
            label="Email"
            id="email"
            type="email"
            value={form.customer.email}
            onChange={(v) => setForm((f) => updateCustomer(f, 'email', v))}
          />
          <Field
            label="Phone"
            id="phone"
            type="tel"
            value={form.customer.phone}
            onChange={(v) => setForm((f) => updateCustomer(f, 'phone', v))}
          />
        </div>
      </section>

      <section className="form-section">
        <h2>Property address</h2>
        <div className="field-grid">
          <Field
            label="Street"
            id="street"
            value={form.customer.street}
            onChange={(v) => setForm((f) => updateCustomer(f, 'street', v))}
          />
          <Field
            label="City"
            id="city"
            value={form.customer.city}
            onChange={(v) => setForm((f) => updateCustomer(f, 'city', v))}
          />
          <Field
            label="State"
            id="state"
            value={form.customer.state}
            onChange={(v) => setForm((f) => updateCustomer(f, 'state', v))}
          />
          <Field
            label="ZIP code"
            id="zip"
            value={form.customer.zip}
            onChange={(v) => setForm((f) => updateCustomer(f, 'zip', v))}
          />
        </div>
      </section>

      <section className="form-section">
        <h2>Service details</h2>
        <div className="field-grid">
          <label className="field" htmlFor="serviceType">
            <span>Service plan</span>
            <select
              id="serviceType"
              value={form.serviceType}
              onChange={(e) =>
                setForm((f) => ({
                  ...f,
                  serviceType: e.target.value as SignupInput['serviceType'],
                }))
              }
            >
              {SERVICE_OPTIONS.map((opt) => (
                <option key={opt.value} value={opt.value}>
                  {opt.label}
                </option>
              ))}
            </select>
          </label>
          <Field
            label="Lot size (sq ft)"
            id="lotSizeSqFt"
            type="number"
            value={form.lotSizeSqFt}
            onChange={(v) =>
              setForm((f) => ({ ...f, lotSizeSqFt: Number(v) || 0 }))
            }
          />
          <Field
            label="Preferred start date"
            id="preferredStartDate"
            type="date"
            value={form.preferredStartDate}
            onChange={(v) => setForm((f) => ({ ...f, preferredStartDate: v }))}
          />
        </div>
      </section>

      {error && <p className="error-banner">{error}</p>}

      <button className="submit-btn" type="submit" disabled={submitting}>
        {submitting ? 'Submitting…' : 'Sign up for lawn service'}
      </button>
    </form>
  );
}
