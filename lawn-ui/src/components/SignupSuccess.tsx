import type { SignupResult } from '../types';
import { SERVICE_OPTIONS } from '../types';

interface SignupSuccessProps {
  result: SignupResult;
  onReset: () => void;
}

function serviceLabel(value: SignupResult['serviceType']) {
  return SERVICE_OPTIONS.find((o) => o.value === value)?.label ?? value;
}

export function SignupSuccess({ result, onReset }: SignupSuccessProps) {
  return (
    <div className="success-card">
      <div className="success-icon">✓</div>
      <h2>Signup received!</h2>
      <p className="success-lead">
        Thanks for signing up. We saved your request and will follow up soon.
      </p>
      <dl className="success-details">
        <div>
          <dt>Signup ID</dt>
          <dd>{result.id}</dd>
        </div>
        <div>
          <dt>Customer ID</dt>
          <dd>{result.customerId}</dd>
        </div>
        <div>
          <dt>Service</dt>
          <dd>{serviceLabel(result.serviceType)}</dd>
        </div>
        <div>
          <dt>Lot size</dt>
          <dd>{result.lotSizeSqFt.toLocaleString()} sq ft</dd>
        </div>
        <div>
          <dt>Start date</dt>
          <dd>{result.preferredStartDate}</dd>
        </div>
        <div>
          <dt>Status</dt>
          <dd>{result.status}</dd>
        </div>
      </dl>
      <button className="submit-btn secondary" type="button" onClick={onReset}>
        Submit another signup
      </button>
    </div>
  );
}
