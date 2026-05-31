export type ServiceType = 'WEEKLY_MOW' | 'BIWEEKLY_MOW' | 'ONE_TIME';

export type SignupStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED';

export interface CustomerInput {
  fullName: string;
  email: string;
  phone: string;
  street: string;
  city: string;
  state: string;
  zip: string;
}

export interface SignupInput {
  customer: CustomerInput;
  serviceType: ServiceType;
  lotSizeSqFt: number;
  preferredStartDate: string;
}

export interface SignupResult {
  id: number;
  customerId: number;
  serviceType: ServiceType;
  lotSizeSqFt: number;
  preferredStartDate: string;
  status: SignupStatus;
  createdAt: string;
}

export const SERVICE_OPTIONS: { value: ServiceType; label: string }[] = [
  { value: 'WEEKLY_MOW', label: 'Weekly mowing' },
  { value: 'BIWEEKLY_MOW', label: 'Every two weeks' },
  { value: 'ONE_TIME', label: 'One-time service' },
];

export const emptyCustomer = (): CustomerInput => ({
  fullName: '',
  email: '',
  phone: '',
  street: '',
  city: '',
  state: '',
  zip: '',
});

export const emptySignup = (): SignupInput => ({
  customer: emptyCustomer(),
  serviceType: 'WEEKLY_MOW',
  lotSizeSqFt: 5000,
  preferredStartDate: '',
});
