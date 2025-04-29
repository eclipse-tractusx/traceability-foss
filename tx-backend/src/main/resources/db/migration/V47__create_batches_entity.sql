-- V3__create_batches_table.sql

CREATE TABLE public.batches (
                                id VARCHAR PRIMARY KEY,
                                batch_id VARCHAR(255),
                                status VARCHAR(50),
                                order_id VARCHAR NOT NULL,
                                CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES public.orders(id) ON DELETE CASCADE
);
