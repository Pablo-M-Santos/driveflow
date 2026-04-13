import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Rate } from 'k6/metrics';

http.setResponseCallback(http.expectedStatuses(201, 409));
export const expectedOutcome = new Rate('expected_outcome');
export const status201 = new Counter('status_201');
export const status409 = new Counter('status_409');
export const status400 = new Counter('status_400');
export const statusOther = new Counter('status_other');

export const options = {
  scenarios: {
    rental_race: {
      executor: 'constant-vus',
      vus: Number(__ENV.VUS || 10),
      duration: __ENV.DURATION || '5s',
      gracefulStop: '0s',
    },
  },
  thresholds: {
    expected_outcome: ['rate==1'],
    status_other: ['count==0'],
    http_req_duration: ['p(95)<500'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080/api/v1';
const CUSTOMER_ID = Number(9);
const VEHICLE_ID = Number(9);
const START_DATE = __ENV.START_DATE || '2026-05-20';
const END_DATE = __ENV.END_DATE || '2026-05-22';
const REQUEST_TIMEOUT = __ENV.REQUEST_TIMEOUT || '10s';

export default function () {
  const url = `${BASE_URL}/rentals`;
  const payload = JSON.stringify({
    customerId: CUSTOMER_ID,
    vehicleId: VEHICLE_ID,
    startDate: START_DATE,
    endDate: END_DATE,
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
    timeout: REQUEST_TIMEOUT,
  };

  const res = http.post(url, payload, params);
  if (res.status === 201) {
    status201.add(1);
  } else if (res.status === 409) {
    status409.add(1);
  } else if (res.status === 400) {
    status400.add(1);
  } else {
    statusOther.add(1);
    console.error(`Unexpected status=${res.status} body=${(res.body || '').slice(0, 200)}`);
  }

  const ok = res.status === 201 || res.status === 409;

  expectedOutcome.add(ok);
  check(res, {
    'status is 201 or 409': () => ok,
  });

  sleep(0.1);
}

