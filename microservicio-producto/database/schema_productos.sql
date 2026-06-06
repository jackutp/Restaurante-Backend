--
-- PostgreSQL database dump
--

\restrict G9CXsrwa5bB1CWM64FLzgQH2JAFCXRVgQEtcUnghwgxwEdDja3a8VnC0nWmDvBR

-- Dumped from database version 18.4
-- Dumped by pg_dump version 18.4

-- Started on 2026-06-06 14:01:39 -05

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 219 (class 1259 OID 17171)
-- Name: productos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.productos (
    productoid integer NOT NULL,
    nombre character varying(100) NOT NULL,
    descripcion character varying(500),
    precio numeric(10,2) NOT NULL,
    categoria character varying(255) NOT NULL,
    imagen_producto character varying(255),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    stock integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.productos OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 17184)
-- Name: productos_productoid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.productos_productoid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.productos_productoid_seq OWNER TO postgres;

--
-- TOC entry 4474 (class 0 OID 0)
-- Dependencies: 220
-- Name: productos_productoid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.productos_productoid_seq OWNED BY public.productos.productoid;


--
-- TOC entry 4314 (class 2604 OID 17250)
-- Name: productos productoid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.productos ALTER COLUMN productoid SET DEFAULT nextval('public.productos_productoid_seq'::regclass);


--
-- TOC entry 4467 (class 0 OID 17171)
-- Dependencies: 219
-- Data for Name: productos; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.productos (productoid, nombre, descripcion, precio, categoria, imagen_producto, created_at, updated_at, stock) FROM stdin;
10	gato pat	12	32.00	PLATO	uploads/6f7f7386-5d1f-449c-93de-2a289cdf9e9f.jpg	2026-05-09 12:18:20.522029	2026-05-09 12:18:20.522029	0
11	Filete carbonizado de oro	un filete bañado en manjar con papas y ensalada rusa	45.00	PLATO	uploads/b5ad61c1-c070-4470-8cb4-ddd7d69f2abe.jpg	2026-05-09 13:18:27.357768	2026-05-09 13:18:27.357768	0
12	Chablis Grand Cru	Vino blanco premium, 2019	119.99	BEBIDA	uploads/592c803a-1c76-465b-929a-ddbb19236878.jpg	2026-05-09 16:21:28.803681	2026-05-09 16:21:28.803681	0
13	Creme Brulee	Vainilla de Madagascar, caramelizado	22.00	POSTRE	uploads/fb2a34d0-0382-44df-9a84-f141257eb373.jpeg	2026-05-09 16:21:48.274188	2026-05-09 16:21:48.274188	0
14	Ravioli de Langosta	Pasta casera, crema de azafran, caviar	38.00	PLATO	uploads/891ff092-852a-4ca0-af3a-ebe2e30a1a03.jpg	2026-05-09 16:33:14.545877	2026-05-09 16:33:14.545877	0
15	Lechuga	diabetes por dulce	500.00	PLATO	uploads/a542b000-2fe0-4302-a371-9b12e9be8f6c.jpg	2026-05-09 16:41:44.553395	2026-05-09 16:41:44.553395	0
16	LAGARTO EXIT	UN LAGARTO PERRON	500.00	BEBIDA	uploads/0aedbe62-417c-46b6-b422-b0e190b86784.jpeg	2026-05-09 17:12:50.233004	2026-05-09 17:12:50.233004	0
\.


--
-- TOC entry 4475 (class 0 OID 0)
-- Dependencies: 220
-- Name: productos_productoid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.productos_productoid_seq', 165, true);


--
-- TOC entry 4319 (class 2606 OID 17187)
-- Name: productos productos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.productos
    ADD CONSTRAINT productos_pkey PRIMARY KEY (productoid);


-- Completed on 2026-06-06 14:01:39 -05

--
-- PostgreSQL database dump complete
--

\unrestrict G9CXsrwa5bB1CWM64FLzgQH2JAFCXRVgQEtcUnghwgxwEdDja3a8VnC0nWmDvBR

