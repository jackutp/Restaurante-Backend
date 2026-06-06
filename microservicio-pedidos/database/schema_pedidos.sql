--
-- PostgreSQL database dump
--

\restrict ERmT3QkHUeHIXuVMI6G1g8HEez1Gp7P3kxYZrIwCDrVlehLhHEzLfgSSwaZPtcY

-- Dumped from database version 18.4
-- Dumped by pg_dump version 18.4

-- Started on 2026-06-06 14:01:19 -05

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
-- TOC entry 219 (class 1259 OID 17382)
-- Name: pedido_items; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pedido_items (
    id bigint NOT NULL,
    pedido_id bigint NOT NULL,
    producto_id integer NOT NULL,
    nombre character varying(255) NOT NULL,
    precio double precision NOT NULL,
    cantidad integer NOT NULL,
    notas character varying(255),
    completado boolean DEFAULT false,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.pedido_items OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 17395)
-- Name: pedido_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.pedido_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.pedido_items_id_seq OWNER TO postgres;

--
-- TOC entry 4489 (class 0 OID 0)
-- Dependencies: 220
-- Name: pedido_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.pedido_items_id_seq OWNED BY public.pedido_items.id;


--
-- TOC entry 221 (class 1259 OID 17396)
-- Name: pedidos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pedidos (
    id bigint NOT NULL,
    orden_id character varying(255) NOT NULL,
    mesa_numero integer NOT NULL,
    hora character varying(255),
    estado character varying(255) DEFAULT 'PENDIENTE'::character varying NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.pedidos OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 17408)
-- Name: pedidos_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.pedidos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.pedidos_id_seq OWNER TO postgres;

--
-- TOC entry 4490 (class 0 OID 0)
-- Dependencies: 222
-- Name: pedidos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.pedidos_id_seq OWNED BY public.pedidos.id;


--
-- TOC entry 4319 (class 2604 OID 17409)
-- Name: pedido_items id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pedido_items ALTER COLUMN id SET DEFAULT nextval('public.pedido_items_id_seq'::regclass);


--
-- TOC entry 4322 (class 2604 OID 17410)
-- Name: pedidos id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pedidos ALTER COLUMN id SET DEFAULT nextval('public.pedidos_id_seq'::regclass);


--
-- TOC entry 4480 (class 0 OID 17382)
-- Dependencies: 219
-- Data for Name: pedido_items; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.pedido_items (id, pedido_id, producto_id, nombre, precio, cantidad, notas, completado, created_at) FROM stdin;
39	36	23	foca	22	2		f	2026-05-20 17:32:24.545139
40	37	23	foca	22	2		f	2026-05-20 17:32:39.396883
\.


--
-- TOC entry 4482 (class 0 OID 17396)
-- Dependencies: 221
-- Data for Name: pedidos; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.pedidos (id, orden_id, mesa_numero, hora, estado, created_at, updated_at) FROM stdin;
36	ORD-1779316344544	11	17:32	PENDIENTE	2026-05-20 17:32:24.544112	2026-05-20 17:32:24.544112
37	ORD-1779316359395	13	17:32	PENDIENTE	2026-05-20 17:32:39.396032	2026-05-20 17:32:39.396032
\.


--
-- TOC entry 4491 (class 0 OID 0)
-- Dependencies: 220
-- Name: pedido_items_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.pedido_items_id_seq', 120, true);


--
-- TOC entry 4492 (class 0 OID 0)
-- Dependencies: 222
-- Name: pedidos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.pedidos_id_seq', 117, true);


--
-- TOC entry 4327 (class 2606 OID 17412)
-- Name: pedido_items pedido_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pedido_items
    ADD CONSTRAINT pedido_items_pkey PRIMARY KEY (id);


--
-- TOC entry 4329 (class 2606 OID 17414)
-- Name: pedidos pedidos_orden_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pedidos
    ADD CONSTRAINT pedidos_orden_id_key UNIQUE (orden_id);


--
-- TOC entry 4331 (class 2606 OID 17416)
-- Name: pedidos pedidos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pedidos
    ADD CONSTRAINT pedidos_pkey PRIMARY KEY (id);


--
-- TOC entry 4332 (class 2606 OID 17417)
-- Name: pedido_items pedido_items_pedido_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pedido_items
    ADD CONSTRAINT pedido_items_pedido_id_fkey FOREIGN KEY (pedido_id) REFERENCES public.pedidos(id) ON DELETE CASCADE;


-- Completed on 2026-06-06 14:01:19 -05

--
-- PostgreSQL database dump complete
--

\unrestrict ERmT3QkHUeHIXuVMI6G1g8HEez1Gp7P3kxYZrIwCDrVlehLhHEzLfgSSwaZPtcY

