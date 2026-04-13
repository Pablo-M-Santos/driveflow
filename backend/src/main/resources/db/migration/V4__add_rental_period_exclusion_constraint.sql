CREATE EXTENSION IF NOT EXISTS btree_gist;

ALTER TABLE rentals
    ADD CONSTRAINT ex_rentals_vehicle_period_active
        EXCLUDE USING gist (
            vehicle_id WITH =,
            daterange(start_date, end_date, '[]') WITH &&
        )
        WHERE (status = 'ACTIVE' AND deleted_at IS NULL);

